package com.whllow.iot.util;

import com.whllow.iot.entity.DeviceData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class CheckUtil {

    public static Map<String,Object> check(DeviceData device,CheckMethod checkMethod){
        Map<String,Object> map = null;
        map = checkMethod.check(device);
        return map;
    }

}
