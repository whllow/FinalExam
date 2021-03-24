package com.whllow.iot.controller.interceptor;

import com.whllow.iot.entity.LoginTicket;
import com.whllow.iot.entity.User;
import com.whllow.iot.service.UserService;
import com.whllow.iot.util.CookieUtil;
import com.whllow.iot.util.HostHolder;
import com.whllow.iot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public HostHolder hostHolder;

    @Autowired
    public UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String str = CookieUtil.getValue(request.getCookies(),"ticket");
        if(str!=null){
            String key = RedisKeyUtil.getTicket(str);
            LoginTicket ticket = (LoginTicket)redisTemplate.opsForValue().get(key);
            if(ticket.getStatus() == 0){
                User user = userService.findUserById(ticket.getUserId());
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null&&modelAndView!=null)
            modelAndView.addObject("loginUser",user);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clean();
    }
}
