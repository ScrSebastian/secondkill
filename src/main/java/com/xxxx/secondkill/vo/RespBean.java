package com.xxxx.secondkill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> RespBean<T> success() {
        return new RespBean<>(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), null);
    }

    public static <T> RespBean<T> success(T data) {
        return new RespBean<>(RespBeanEnum.SUCCESS.getCode(), RespBeanEnum.SUCCESS.getMessage(), data);
    }

    public static <T> RespBean<T> error(RespBeanEnum respBeanEnum) {
        return new RespBean<>(respBeanEnum.getCode(), respBeanEnum.getMessage(), null);
    }
}
