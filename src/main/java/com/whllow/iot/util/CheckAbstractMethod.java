package com.whllow.iot.util;

import com.whllow.iot.entity.DeviceData;

import java.util.HashMap;
import java.util.Map;

public abstract class CheckAbstractMethod implements CheckMethod{

    @Override
    public Map<String, Object> checkNull(DeviceData deviceData) {
        Map<String,Object> map = new HashMap<>();
        if(deviceData.getPh() == 0) map.put("phMsg","PH传感器出现故障");
        if(deviceData.getTds() == 0) map.put("tdsMsg","TDS传感器出现故障");
        if(deviceData.getTemperature() == 0) map.put("temperatureMsg","温度传感器出现故障");
        return map;
    }

    @Override
    public Map<String, Object> check(DeviceData deviceData) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> tmp = checkNull(deviceData);
        if(!tmp.isEmpty()) map.putAll(tmp);
        if(!map.containsKey("phMsg")) map.put("phMsg",checkPh(deviceData.getPh()));
        if(!map.containsKey("tdsMsg")) map.put("tdsMsg",checkTds(deviceData.getTds()));
        if(!map.containsKey("temperatureMsg")) map.put("temperatureMsg",checkTemperature(deviceData.getTemperature())) ;
        return map;
    }
}
