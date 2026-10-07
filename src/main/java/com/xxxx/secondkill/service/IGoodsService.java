package com.xxxx.secondkill.service;

import com.xxxx.secondkill.pojo.Goods;
import com.xxxx.secondkill.vo.GoodsVo;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 查询全部商品及其可选的秒杀活动信息。
     *
     * @return 商品秒杀视图列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 查询指定商品详情及其可选的秒杀活动信息。
     *
     * @param goodsId 商品 ID
     * @return 商品详情；不存在时返回 {@code null}
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
