package com.xxxx.secondkill.service;

import com.xxxx.secondkill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.vo.GoodsVo;
import com.xxxx.secondkill.vo.OrderDetailVo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
public interface IOrderService extends IService<Order> {

    Order sekill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);
}
