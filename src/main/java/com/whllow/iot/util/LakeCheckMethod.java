package com.whllow.iot.util;

import org.springframework.stereotype.Component;

//具体的水样检测方法
@Component
public class LakeCheckMethod extends CheckAbstractMethod {

    @Override
    public String checkPh(float ph) {
        return null;
    }

    @Override
    public String checkTds(float tds) {
        return null;
    }

    @Override
    public String checkTemperature(float temperature) {
        return null;
    }
}
