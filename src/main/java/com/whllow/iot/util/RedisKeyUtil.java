package com.whllow.iot.util;

import com.whllow.iot.entity.IotConstance;

public class RedisKeyUtil implements IotConstance {

    //生成user的key
    public static String getUser(int userid){
        return PREFIX_USER+SPLIT+userid;
    }

    //生成验证码的key
    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA+SPLIT + owner;
    }

    //生成登录凭证的key
    public static String getTicket(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    //生成忘记密码的验证码的key
    public static String getRreshPassword(String owner) {
        return PREFIX_KAPTCHA+SPLIT + owner;
    }

    //生成最新数据的key
    public static String getDeviceData(String deviceId) {
        return PREFIX_DEVICE+SPLIT + deviceId;
    }

    //生成设备状态的key
    public static String getDeviceStatus(String deviceId) {
        return PREFIX_DEVICESTATUS+SPLIT + deviceId;
    }

    //生成设备故障的key
    public static String getDeviceWarning(String deviceId) {
        return PREFIX_WARNING+SPLIT + deviceId;
    }

}
