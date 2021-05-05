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
        /*
        * 下面的操作是为了处理你登录一个网站，只要不自动登出，你再一次访问网站，你就不需要再登录
        *
        * 实现逻辑：
        * 通过Cookie存储相关凭证的键值，在访问网站的时候，Http的Request会携带缓存在浏览器中的所有Cookie，传递到
        * 服务器中，找到对应的键值，在通过RedisKeyUntil转换为Redis的键值，从Redis中取出对应凭证，看看这个凭证是否
        * 超时
        * */

        String str = CookieUtil.getValue(request.getCookies(),"ticket");//解析工作。
        if(str!=null){
            String key = RedisKeyUtil.getTicket(str);
            LoginTicket ticket = (LoginTicket)redisTemplate.opsForValue().get(key);
            if(ticket!=null&&ticket.getStatus() == 0){
                User user = userService.findUserById(ticket.getUserId());//通过User的id去MySQL中，获取User的信息
                hostHolder.setUser(user);//存储登录对象
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null&&modelAndView!=null)
            modelAndView.addObject("loginUser",user);
        //提前向每一个界面（HTML）注入登录对象，
        // 都可以LoginUser这个键值获取User的相关信息（前端）

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clean();//登出，清除user用户
    }
}
