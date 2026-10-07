package com.xxxx.secondkill.config;

import com.xxxx.secondkill.pojo.User;
import com.xxxx.secondkill.service.IUserService;
import com.xxxx.secondkill.utils.CookieUtil;
import org.springframework.stereotype.Component;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String USER_ATTRIBUTE = UserArgumentResolver.class.getName() + ".USER";

    private final IUserService userService;

    public UserArgumentResolver(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        if (request == null || response == null) {
            return null;
        }

        Object cachedUser = request.getAttribute(USER_ATTRIBUTE);
        if (cachedUser instanceof User) {
            return cachedUser;
        }

        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        User user = userService.getUserByCookie(ticket, request, response);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login/toLogin");
            if (mavContainer != null) {
                mavContainer.setRequestHandled(true);
            }
            return null;
        }
        request.setAttribute(USER_ATTRIBUTE, user);
        return user;
    }
}
