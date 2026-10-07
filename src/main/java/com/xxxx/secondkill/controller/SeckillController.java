package com.xxxx.secondkill.controller;

import com.xxxx.secondkill.pojo.SeckillMessage;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.rabbitmq.MQSender;
import com.xxxx.secondkill.service.IGoodsService;
import com.xxxx.secondkill.service.ISeckillOrderService;
import com.xxxx.secondkill.service.SeckillAdmissionService;
import com.xxxx.secondkill.utils.UUIDUtil;
import com.xxxx.secondkill.utils.exception.GlobalException;
import com.xxxx.secondkill.vo.GoodsVo;
import com.xxxx.secondkill.vo.RespBean;
import com.xxxx.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/secKill")
public class SeckillController implements InitializingBean {

    private final IGoodsService goodsService;
    private final ISeckillOrderService seckillOrderService;
    private final SeckillAdmissionService admissionService;
    private final MQSender mqSender;

    public SeckillController(IGoodsService goodsService,
                             ISeckillOrderService seckillOrderService,
                             SeckillAdmissionService admissionService,
                             MQSender mqSender) {
        this.goodsService = goodsService;
        this.seckillOrderService = seckillOrderService;
        this.admissionService = admissionService;
        this.mqSender = mqSender;
    }

    @PostMapping("/path")
    @ResponseBody
    public RespBean<String> getPath(User user, @RequestParam Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        validateActiveGoods(goodsId);
        String path = admissionService.createPath(user.getId(), goodsId);
        if (path == null) {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }
        return RespBean.success(path);
    }

    @PostMapping("/{path}/doSecKill")
    @ResponseBody
    public RespBean<Long> doSecKill(User user,
                                    @PathVariable String path,
                                    @RequestParam Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        validateActiveGoods(goodsId);

        SeckillAdmissionService.AdmissionResult result =
                admissionService.admit(user.getId(), goodsId, path);
        if (result == SeckillAdmissionService.AdmissionResult.INVALID_PATH) {
            return RespBean.error(RespBeanEnum.PATH_ERROR);
        }
        if (result == SeckillAdmissionService.AdmissionResult.DUPLICATE) {
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        if (result == SeckillAdmissionService.AdmissionResult.SOLD_OUT) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage message = new SeckillMessage(UUIDUtil.uuid(), user.getId(), goodsId);
        if (!mqSender.sendSeckillMessage(message)) {
            admissionService.releaseReservation(user.getId(), goodsId);
            return RespBean.error(RespBeanEnum.MQ_SEND_ERROR);
        }
        return RespBean.success(0L);
    }

    @GetMapping("/result")
    @ResponseBody
    public RespBean<Long> result(User user, @RequestParam Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        return RespBean.success(seckillOrderService.getResult(user, goodsId));
    }

    @GetMapping("/toFail")
    public String toFail(@RequestParam(value = "reason", required = false) String reason, Model model) {
        model.addAttribute("errMsg", reason);
        return "secKillFail";
    }

    @Override
    public void afterPropertiesSet() {
        List<GoodsVo> goodsList = goodsService.findGoodsVo();
        for (GoodsVo goods : goodsList) {
            admissionService.initializeStockIfAbsent(goods.getId(), goods.getStockCount());
        }
    }

    private GoodsVo validateActiveGoods(Long goodsId) {
        if (goodsId == null) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if (goods == null || goods.getSeckillPrice() == null
                || goods.getStartDate() == null || goods.getEndDate() == null) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(goods.getStartDate())) {
            throw new GlobalException(RespBeanEnum.SECKILL_NOT_STARTED);
        }
        if (!now.isBefore(goods.getEndDate())) {
            throw new GlobalException(RespBeanEnum.SECKILL_ENDED);
        }
        return goods;
    }
}
