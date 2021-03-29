package com.whllow.iot.dao;


import com.whllow.iot.entity.Device;
import com.whllow.iot.entity.DeviceData;
import com.whllow.iot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DeviceMapper {

    //通过user的ID查看该用户拥有的设备
    List<Device> selectDevicesByUserId(int userId);

    //通过设备ID来获取设备的基本信息
    Device selectDeviceByDeviceId(String deviceId);

}
