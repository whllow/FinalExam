package com.whllow.iot.util;

import com.whllow.iot.entity.DeviceData;

import java.util.Map;

public interface CheckMethod {

    String checkPh(float ph);

    String checkTds(float tds);

    String checkTemperature(float temperature);

    Map<String,Object> checkNull(DeviceData deviceData);

    Map<String,Object> check(DeviceData deviceData);
}
