package com.xxxx.secondkill.vo;

import com.xxxx.secondkill.pojo.Order;
import lombok.Data;

/**
 * 订单详情接口需要的数据。
 */
@Data
public class OrderDetailVo {

    /** 订单信息。 */
    private Order order;

    /** 订单对应的商品及秒杀活动信息。 */
    private GoodsVo goodsVo;
}
