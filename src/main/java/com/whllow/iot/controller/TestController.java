package com.whllow.iot.controller;

import com.whllow.iot.entity.TestData;
import com.whllow.iot.util.IotUntil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class TestController {


    @RequestMapping(path = "/testMachine/{deviceId}",method = RequestMethod.GET)
    public String getMachinePage(Model model,@PathVariable("deviceId")String deviceId){
        model.addAttribute("deviceId",deviceId);
        return "/site/machine";
    }






}
