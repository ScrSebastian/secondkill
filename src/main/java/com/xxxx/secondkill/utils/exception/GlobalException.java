package com.xxxx.secondkill.utils.exception;

import com.xxxx.secondkill.vo.RespBeanEnum;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final RespBeanEnum respBeanEnum;

    public GlobalException(RespBeanEnum respBeanEnum) {
        super(respBeanEnum.getMessage());
        this.respBeanEnum = respBeanEnum;
    }
}
