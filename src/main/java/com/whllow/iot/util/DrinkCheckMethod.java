package com.whllow.iot.util;

import org.springframework.stereotype.Component;

@Component
public class DrinkCheckMethod extends CheckAbstractMethod {
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
