package com.xxxx.secondkill.vo;

import com.xxxx.secondkill.pojo.User;
import lombok.Data;

/**
 * 商品详情页需要的数据。
 */
@Data
public class DetailVo {

    /** 当前登录用户。 */
    private User user;

    /** 商品及其秒杀活动信息。 */
    private GoodsVo goodsVo;

    /** 秒杀状态：0 未开始，1 进行中，2 已结束。 */
    private int secKillStatus;

    /** 距离秒杀开始的剩余秒数。 */
    private int remainSeconds;
}
