package com.xxxx.secondkill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.secondkill.pojo.SeckillOrder;
import com.xxxx.secondkill.mapper.SeckillOrderMapper;
import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.ISeckillOrderService;
import com.xxxx.secondkill.service.SeckillAdmissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 秒杀订单表 服务实现类
 * </p>
 *
 * @author seb
 * @since 2026-08-06
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    private final SeckillOrderMapper seckillOrderMapper;
    private final SeckillAdmissionService admissionService;

    public SeckillOrderServiceImpl(SeckillOrderMapper seckillOrderMapper,
                                   SeckillAdmissionService admissionService) {
        this.seckillOrderMapper = seckillOrderMapper;
        this.admissionService = admissionService;
    }

    @Override
    public Long getResult(User user, Long goodsId) {
        Long cachedResult = admissionService.getCachedResult(user.getId(), goodsId);
        if (cachedResult != null) {
            return cachedResult;
        }
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("user_id",user.getId()).eq("goods_id",goodsId));
        if(seckillOrder!=null){
            admissionService.markSuccess(user.getId(), goodsId, seckillOrder.getOrderId());
            return seckillOrder.getOrderId();
        }
        return 0L;
    }
}
