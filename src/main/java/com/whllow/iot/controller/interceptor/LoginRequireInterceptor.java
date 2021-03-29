package com.whllow.iot.controller.interceptor;

import com.whllow.iot.annotation.LoginRequired;
import com.whllow.iot.entity.User;
import com.whllow.iot.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    @Autowired
    public HostHolder hostHolder;//用于存储当前用户，防止多线程中用户错乱

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //前置处理器
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired !=null&&hostHolder.getUser() == null){//判断是否登录了
             response.sendRedirect(request.getContextPath()+"/login");//没有登录就重定向到登录界面
             return false;//阻断该次访问
            }
        }
        return true;//允许继续访问
    }
}
