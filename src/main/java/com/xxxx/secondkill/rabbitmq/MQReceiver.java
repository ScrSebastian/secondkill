package com.xxxx.secondkill.rabbitmq;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.secondkill.config.RabbitMQConfig;
import com.xxxx.secondkill.pojo.Order;
import com.xxxx.secondkill.pojo.SeckillMessage;
import com.xxxx.secondkill.pojo.SeckillOrder;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.IGoodsService;
import com.xxxx.secondkill.service.IOrderService;
import com.xxxx.secondkill.service.ISeckillOrderService;
import com.xxxx.secondkill.service.SeckillAdmissionService;
import com.xxxx.secondkill.utils.JSONUtil;
import com.xxxx.secondkill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    private final IGoodsService goodsService;
    private final IOrderService orderService;
    private final ISeckillOrderService seckillOrderService;
    private final SeckillAdmissionService admissionService;

    public MQReceiver(IGoodsService goodsService,
                      IOrderService orderService,
                      ISeckillOrderService seckillOrderService,
                      SeckillAdmissionService admissionService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.seckillOrderService = seckillOrderService;
        this.admissionService = admissionService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String message) {
        SeckillMessage seckillMessage = JSONUtil.jsonStr2Object(message, SeckillMessage.class);
        if (seckillMessage == null || seckillMessage.getUserId() == null
                || seckillMessage.getGoodsId() == null) {
            log.error("丢弃无效秒杀消息");
            return;
        }

        Long userId = seckillMessage.getUserId();
        Long goodsId = seckillMessage.getGoodsId();
        SeckillOrder existingOrder = findExistingOrder(userId, goodsId);
        if (existingOrder != null) {
            admissionService.markSuccess(userId, goodsId, existingOrder.getOrderId());
            return;
        }

        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods == null || goods.getSeckillPrice() == null) {
            admissionService.failAndReleaseReservation(userId, goodsId);
            return;
        }

        User user = new User();
        user.setId(userId);
        try {
            Order order = orderService.sekill(user, goods);
            if (order == null) {
                // MySQL 是最终库存真相。此时不能恢复 Redis 库存，否则会反复放行无库存请求。
                admissionService.markFailed(userId, goodsId);
                return;
            }
            admissionService.markSuccess(userId, goodsId, order.getId());
        } catch (DuplicateKeyException exception) {
            SeckillOrder duplicateOrder = findExistingOrder(userId, goodsId);
            if (duplicateOrder == null) {
                throw exception;
            }
            admissionService.markSuccess(userId, goodsId, duplicateOrder.getOrderId());
        }
    }

    private SeckillOrder findExistingOrder(Long userId, Long goodsId) {
        return seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", userId)
                .eq("goods_id", goodsId));
    }
}
