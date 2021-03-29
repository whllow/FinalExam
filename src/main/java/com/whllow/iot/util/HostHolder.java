package com.whllow.iot.util;

import com.whllow.iot.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //保证多线程中获取的user是一份，保证线程安全
    ThreadLocal<User> users = new ThreadLocal<>();
    //存储登录对象
    public void setUser(User user){
        users.set(user);
    }
    //获取已登录对象
    public User getUser(){
        return users.get();
    }
    //移除登录对象
    public void clean(){
        users.remove();
    }
}
