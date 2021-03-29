package com.whllow.iot.util;

import com.whllow.iot.entity.DeviceData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
//策略模式
public class CheckUtil {
    //通过注入的策略来检测水样，解耦和，提高了系统的扩展性
    public static Map<String,Object> check(DeviceData device,CheckMethod checkMethod){
        Map<String,Object> map = null;
        map = checkMethod.check(device);
        return map;
    }

}
