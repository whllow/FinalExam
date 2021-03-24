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

    List<Device> selectDevicesByUserId(int userId);

    Device selectDeviceByDeviceId(String deviceId);

}
