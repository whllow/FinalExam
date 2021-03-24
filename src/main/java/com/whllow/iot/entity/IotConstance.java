package com.whllow.iot.entity;

public interface IotConstance {

    /*
     * 激活成功
     **/
    int ACTIVATION_SUCCESS = 0;

    /**
     *
     * 重复激活
     *
     * */

    int ACTIVATION_REPEAT = 1;

    /**
     *
     * 激活失败
     *
     * */

    int ACTIVATION_FAILURE = 2;

    /**
     *
     * 默认有效时间
     *
     * */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     *
     * 记住我的有效时间
     *
     * */
    int REMEMBER_EXPIRED_SECONDS =3600 * 24 * 100;


    /**
     *
     * Redis key
     *
     *
     * */
    String SPLIT = ":";
    String PREFIX_KAPTCHA = "kaptcha";
    String PREFIX_USER = "user";
    String PREFIX_TICKET = "ticket";
    String PREFIX_PASSWORD = "password";
    String PREFIX_DEVICE = "device";
    String PREFIX_DEVICESTATUS = "deviceStatus";
    String PREFIX_WARNING = "warning";


    /**
     *
     * 维修设备时间
     *
     * */
    int REPAIR_DEVICE_SECONDS = 3600 * 24 * 14;

}
