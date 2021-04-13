package com.whllow.iot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    @RequestMapping(path = "/",method = RequestMethod.GET)
    public String root(){
        return "forward:/index";//获取首页
    }


    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndex(){
        return "index";//获取首页
    }

}
