package com.whllow.iot.dao;

import com.whllow.iot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface UserMapper {

    User selelctUserById(int id);

    public User selectUserByUsername(String username);

    public User selectUserByEmail(String email);

    public int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);

}
