package com.xxxx.secondkill.controller;

import com.xxxx.secondkill.vo.LoginVO;
import com.xxxx.secondkill.vo.RespBean;
import com.xxxx.secondkill.service.IUserService;
import com.xxxx.secondkill.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {

    private final IUserService userService;

    public LoginController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/toLogin")
    public String toLogin(Model model) {
        model.addAttribute("inputPassSalt", MD5Util.INPUT_PASS_SALT);
        return "login";
    }

    @PostMapping("/doLogin")
    @ResponseBody
    public RespBean<?> doLogin(@RequestBody LoginVO loginVO, HttpServletRequest request,
                               HttpServletResponse response) {
        userService.doLogin(loginVO, request, response);
        return RespBean.success();
    }
}
