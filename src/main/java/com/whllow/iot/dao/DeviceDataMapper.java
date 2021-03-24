package com.whllow.iot.dao;

import com.whllow.iot.entity.DeviceData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DeviceDataMapper {

    int insertDeviceData(DeviceData data);

    DeviceData selelctDataByDeviceId(String deviceId);

    List<DeviceData> selelctDatasByDeviceId(String deviceId);

}
