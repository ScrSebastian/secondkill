package com.xxxx.secondkill.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.xxxx.secondkill.pojo.Goods;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 商品与秒杀活动的聚合视图。
 */
@Getter
@Setter
@ToString(callSuper = true)
public class GoodsVo extends Goods {

    /** 秒杀价格。 */
    private BigDecimal seckillPrice;

    /** 秒杀活动库存。 */
    private Integer stockCount;

    /** 秒杀开始时间。 */
    private LocalDateTime startDate;

    /** 秒杀结束时间。 */
    private LocalDateTime endDate;
}
