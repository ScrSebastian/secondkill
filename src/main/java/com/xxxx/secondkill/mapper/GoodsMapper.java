package com.xxxx.secondkill.mapper;

import com.xxxx.secondkill.pojo.Goods;
import com.xxxx.secondkill.vo.GoodsVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 查询商品及对应的秒杀活动信息。
     *
     * @return 商品秒杀视图列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 根据商品 ID 查询商品及其秒杀活动信息。
     *
     * @param goodsId 商品 ID
     * @return 商品秒杀视图；商品不存在时返回 {@code null}
     */
    GoodsVo findGoodsVoByGoodsId(@Param("goodsId") Long goodsId);
}
