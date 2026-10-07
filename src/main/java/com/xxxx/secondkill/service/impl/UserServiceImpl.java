package com.xxxx.secondkill.service.impl;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.mapper.UserMapper;
import com.xxxx.secondkill.service.IUserService;
import com.xxxx.secondkill.utils.CookieUtil;
import com.xxxx.secondkill.utils.MD5Util;
import com.xxxx.secondkill.utils.SessionConstants;
import com.xxxx.secondkill.utils.UUIDUtil;
import com.xxxx.secondkill.utils.ValidatorUtil;
import com.xxxx.secondkill.utils.exception.GlobalException;
import com.xxxx.secondkill.vo.LoginVO;
import com.xxxx.secondkill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author seb
 * @since 2026-07-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserServiceImpl(UserMapper userMapper, RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void doLogin(LoginVO loginVO, HttpServletRequest request, HttpServletResponse response) {
        if (!ValidatorUtil.isMobile(loginVO.getMobile())) {
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        }
        if (loginVO.getPassword() == null || loginVO.getPassword().isEmpty()) {
            throw new GlobalException(RespBeanEnum.PASSWORD_ERROR);
        }

        User user = userMapper.selectById(Long.valueOf(loginVO.getMobile()));
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }

        String databasePassword = MD5Util.fromPassToDBPass(loginVO.getPassword(), user.getSalt());
        if (!databasePassword.equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.PASSWORD_ERROR);
        }

        user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
        user.setLastLoginDate(new Date());
        updateById(user);

        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set(
                SessionConstants.userSessionKey(ticket), user, SessionConstants.SESSION_TIMEOUT);

        CookieUtil.setCookie(request, response, "userTicket", ticket);
    }

    @Override
    public User getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(ticket)) {
            return null;
        }

        String sessionKey = SessionConstants.userSessionKey(ticket);
        User user = (User) redisTemplate.opsForValue().get(sessionKey);
        if (user != null) {
            redisTemplate.expire(sessionKey, SessionConstants.SESSION_TIMEOUT);
            CookieUtil.setCookie(request, response, "userTicket", ticket);
        }
        return user;
    }

}
