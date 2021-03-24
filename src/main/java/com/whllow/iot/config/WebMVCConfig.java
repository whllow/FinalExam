package com.whllow.iot.config;

import com.whllow.iot.annotation.LoginRequired;
import com.whllow.iot.controller.interceptor.LoginRequireInterceptor;
import com.whllow.iot.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Autowired
    public LoginRequireInterceptor loginRequireInterceptor;

    @Autowired
    public LoginTicketInterceptor loginTicketInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册拦截器到Servlet中去，主要是为了判断用户是否登录
        //下面两个类在Controller文件下的Interceptor文件夹中有具体的说明。
        // excludePathPatterns：配置该拦截器不拦截的资源的路径，例如（在resource中static静态资源）/css/*.css：
        // 不拦截/css/文件夹下.css结尾的资源，一般拦截器不拦截静态资源，不然网页无法加载出来
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png","/img/*.jpg",
                        "/img/*.jpeg");
        registry.addInterceptor(loginRequireInterceptor)//是下面的静态资源就不拦截
                .excludePathPatterns("/css/*.css","/js/*.js","/img/*.png","/img/*.jpg",
                        "/img/*.jpeg");

    }
}
