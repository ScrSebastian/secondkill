package com.xxxx.secondkill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RespBeanEnum {

    SUCCESS(200, "成功"),
    ERROR(500, "服务器异常"),
    BIND_ERROR(500101, "参数校验异常"),
    REQUEST_ILLEGAL(500102, "请求非法"),
    SESSION_ERROR(500210, "用户未登录或登录状态失效"),
    MOBILE_ERROR(500211, "手机号格式错误"),
    MOBILE_NOT_EXIST(500212, "手机号不存在"),
    PASSWORD_ERROR(500213, "密码错误"),

    EMPTY_STOCK(500301,"秒杀商品库存不足"),
    REPEAT_ERROR(500302,"重复下单"),
    ORDER_NOT_EXIST(500303,"订单不存在"),
    SECKILL_NOT_STARTED(500304,"秒杀活动尚未开始"),
    SECKILL_ENDED(500305,"秒杀活动已经结束"),
    PATH_ERROR(500306,"秒杀地址无效或已过期"),
    ACCESS_LIMIT_REACHED(500307,"请求过于频繁，请稍后重试"),
    MQ_SEND_ERROR(500308,"秒杀请求提交失败，请重试")
    ;

    private final Integer code;

    private final String message;
}
