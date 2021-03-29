package com.whllow.iot.util;

import com.whllow.iot.entity.DeviceData;

import java.util.Map;

public interface CheckMethod {
    //检测ph值是否异常
    String checkPh(float ph);
    //检测固体颗粒浓度是否异常
    String checkTds(float tds);
    //检测温度是否异常
    String checkTemperature(float temperature);
    //空值判断
    Map<String,Object> checkNull(DeviceData deviceData);
    //检测方法
    Map<String,Object> check(DeviceData deviceData);
}
