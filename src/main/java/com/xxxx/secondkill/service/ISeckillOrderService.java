package com.xxxx.secondkill.service;

import com.xxxx.secondkill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xxxx.secondkill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {


    Long getResult(User user, Long goodsId);
}
