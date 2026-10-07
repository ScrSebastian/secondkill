package com.xxxx.secondkill.service;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.vo.LoginVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author seb
 * @since 2026-07-18
 */
public interface IUserService extends IService<User> {

    void doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response);
}
