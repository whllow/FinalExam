package com.whllow.iot.entity;

import java.util.Date;
//登录凭证
public class LoginTicket {

    private int id;//ID号
    private int userId;//用户ID
    private String ticket;//凭证（随机数），通过Cookie传输到浏览器中缓存
    private int status;//凭证状态0为失效，1为有效
    private Date expire;//过期时间

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

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expire=" + expire +
                '}';
    }
}
