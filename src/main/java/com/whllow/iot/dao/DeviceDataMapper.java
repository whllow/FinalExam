package com.whllow.iot.dao;

import com.whllow.iot.entity.DeviceData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface DeviceDataMapper {

    //将最新的数据插入的数据库中
    int insertDeviceData(DeviceData data);

    //通过设备的ID来搜索设备的最新数据
    DeviceData selelctDataByDeviceId(String deviceId);

    //通过设备的ID来搜索设备的历史数据（20条）
    List<DeviceData> selelctDatasByDeviceId(String deviceId);

}
