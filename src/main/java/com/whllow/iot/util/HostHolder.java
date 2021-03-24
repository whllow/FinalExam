package com.whllow.iot.util;

import com.whllow.iot.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }
    public User getUser(){
        return users.get();
    }

    public void clean(){
        users.remove();
    }
}
