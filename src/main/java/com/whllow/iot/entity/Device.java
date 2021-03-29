package com.whllow.iot.entity;

//设备基本的信息
public class Device {

    private int id;//ID号
    private int userId;//userID
    private String deviceId;//设备ID
    private int status;//设备状态 0为离线，1为正常，2为故障
    private String purpose;//用途
    private String other;//备注
    private String activationCode;//激活码
    private double longitude;//经度
    private double latitude;//纬度

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Device(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", userId=" + userId +
                ", deviceId='" + deviceId + '\'' +
                ", status=" + status +
                ", purpose='" + purpose + '\'' +
                ", other='" + other + '\'' +
                ", activationCode='" + activationCode + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }
}
