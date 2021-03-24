package com.whllow.iot.util;

import com.whllow.iot.entity.IotConstance;

public class RedisKeyUtil implements IotConstance {


    public static String getUser(int userid){
        return PREFIX_USER+SPLIT+userid;
    }

    public static String getKaptcha(String owner){
        return PREFIX_KAPTCHA+SPLIT + owner;
    }
    public static String getTicket(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getRreshPassword(String owner) {
        return PREFIX_KAPTCHA+SPLIT + owner;
    }

    public static String getDeviceData(String deviceId) {
        return PREFIX_DEVICE+SPLIT + deviceId;
    }

    public static String getDeviceStatus(String deviceId) {
        return PREFIX_DEVICESTATUS+SPLIT + deviceId;
    }
    public static String getDeviceWarning(String deviceId) {
        return PREFIX_WARNING+SPLIT + deviceId;
    }

}
