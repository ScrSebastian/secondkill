package com.xxxx.secondkill.controller;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.IOrderService;
import com.xxxx.secondkill.vo.OrderDetailVo;
import com.xxxx.secondkill.vo.RespBean;
import com.xxxx.secondkill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @GetMapping("/toDetail")
    public String toDetail() {
        return "orderDetail";
    }

    @GetMapping("/detail")
    @ResponseBody
    public RespBean<OrderDetailVo> detail(User user, Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        if (!user.getId().equals(detail.getOrder().getUserId())) {
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
        }
        return RespBean.success(detail);
    }

}
