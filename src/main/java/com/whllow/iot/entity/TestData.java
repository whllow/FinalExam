package com.whllow.iot.entity;

public class TestData {

    private String deviceId;
    private int status;
    private String purpose;
    private String other;


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    @Override
    public String toString() {
        return "test{" +
                "deviceId='" + deviceId + '\'' +
                ", status=" + status +
                ", purpose='" + purpose + '\'' +
                ", other='" + other + '\'' +
                '}';
    }
}
