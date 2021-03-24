package com.whllow.iot.entity;

import java.util.Date;

public class DeviceData {

    private String deviceId;
    private float ph;
    private float tds;
    private float temperature;
    private Date deviceTime;

    public DeviceData(){}

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getPh() {
        return ph;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public float getTds() {
        return tds;
    }

    public void setTds(float tds) {
        this.tds = tds;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public Date getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(Date deviceTime) {
        this.deviceTime = deviceTime;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "deviceId='" + deviceId + '\'' +
                ", ph=" + ph +
                ", tds=" + tds +
                ", temperature=" + temperature +
                ", deviceTime=" + deviceTime +
                '}';
    }
}
