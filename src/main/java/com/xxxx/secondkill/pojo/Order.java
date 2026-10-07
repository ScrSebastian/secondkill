package com.xxxx.secondkill.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
@Getter
@Setter
@ToString
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 收货地址ID
     */
    @TableField("delivery_addr_id")
    private Long deliveryAddrId;

    /**
     * 冗余过来的商品名称
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 商品数量
     */
    @TableField("goods_count")
    private Integer goodsCount;

    /**
     * 商品单价
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 1pc，2android，3ios
     */
    @TableField("order_channel")
    private Byte orderChannel;

    /**
     * 订单状态：0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成
     */
    @TableField("status")
    private Byte status;

    /**
     * 订单的创建时间
     */
    @TableField("create_date")
    private LocalDateTime createDate;

    /**
     * 支付时间
     */
    @TableField("pay_date")
    private LocalDateTime payDate;

}
