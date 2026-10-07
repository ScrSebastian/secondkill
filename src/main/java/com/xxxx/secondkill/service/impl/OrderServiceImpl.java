package com.xxxx.secondkill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xxxx.secondkill.pojo.Order;
import com.xxxx.secondkill.mapper.OrderMapper;
import com.xxxx.secondkill.pojo.SeckillGoods;
import com.xxxx.secondkill.pojo.SeckillOrder;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.IGoodsService;
import com.xxxx.secondkill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxxx.secondkill.service.ISeckillGoodsService;
import com.xxxx.secondkill.service.ISeckillOrderService;
import com.xxxx.secondkill.utils.exception.GlobalException;
import com.xxxx.secondkill.vo.GoodsVo;
import com.xxxx.secondkill.vo.OrderDetailVo;
import com.xxxx.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Transactional
    @Override
    public Order sekill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id",goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
//        seckillGoodsService.updateById(seckillGoods);
        boolean success = seckillGoodsService.update(
                new UpdateWrapper<SeckillGoods>()
                        .setSql("stock_count = stock_count - 1")
                        .eq("id", seckillGoods.getId())
                        .gt("stock_count", 0)
        );
        if(!success){
            return null;
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel((byte) 1);
        order.setStatus((byte) 0);
        order.setCreateDate(LocalDateTime.now());

        orderMapper.insert(order);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());

        seckillOrderService.save(seckillOrder);
        return order;


    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);

        return orderDetailVo;
    }
}
