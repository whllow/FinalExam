package com.whllow.iot.entity;

public class Mistake {

    private int id;
    private int uesrId;
    private String userId;
    private String mistakeMessage;

    public Mistake(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUesrId() {
        return uesrId;
    }

    public void setUesrId(int uesrId) {
        this.uesrId = uesrId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMistakeMessage() {
        return mistakeMessage;
    }

    public void setMistakeMessage(String mistakeMessage) {
        this.mistakeMessage = mistakeMessage;
    }

    @Override
    public String toString() {
        return "Mistake{" +
                "id=" + id +
                ", uesrId=" + uesrId +
                ", userId='" + userId + '\'' +
                ", mistakeMessage='" + mistakeMessage + '\'' +
                '}';
    }
}
