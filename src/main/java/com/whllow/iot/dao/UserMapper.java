package com.whllow.iot.dao;

import com.whllow.iot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {

    //通过user的ID来获取user的基本信息
    public User selelctUserById(int id);

    //通过user的username来获取user的基本信息
    public User selectUserByUsername(String username);

    //通过user的email来获取user的基本信息
    public User selectUserByEmail(String email);

    //存入user的基本信息
    public int insertUser(User user);

    //更新user的状态
    int updateStatus(int id,int status);

    //更新user的头像
    int updateHeader(int id,String headerUrl);

    //更换密码
    int updatePassword(int id,String password);

}
