package com.xxxx.secondkill.controller;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.rabbitmq.MQSender;
import com.xxxx.secondkill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author seb
 * @since 2026-07-18
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){ return RespBean.success(user);}

//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("Hello World");
//    }

}
