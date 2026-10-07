package com.xxxx.secondkill.utils.exception;

import com.xxxx.secondkill.vo.RespBean;
import com.xxxx.secondkill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public RespBean<?> handleGlobalException(GlobalException exception) {
        return RespBean.error(exception.getRespBeanEnum());
    }

    @ExceptionHandler(Exception.class)
    public RespBean<?> handleException(Exception exception) {
        log.error("未处理的系统异常", exception);
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
