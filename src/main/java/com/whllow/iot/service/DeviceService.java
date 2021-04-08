package com.whllow.iot.service;

import com.sun.jdi.ObjectCollectedException;
import com.whllow.iot.dao.DeviceDataMapper;
import com.whllow.iot.dao.DeviceMapper;
import com.whllow.iot.dao.UserMapper;
import com.whllow.iot.entity.Device;
import com.whllow.iot.entity.DeviceData;
import com.whllow.iot.entity.IotConstance;
import com.whllow.iot.entity.User;
import com.whllow.iot.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DeviceService implements IotConstance {

    @Autowired
    public DeviceDataMapper deviceDataMapper;

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;


    @Autowired
    public HostHolder hostHolder;

    @Autowired
    public DeviceMapper deviceMapper;

    @Autowired
    public MailClient mailClient;

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public TemplateEngine templateEngine;

    /**
     * {"device_id":"Esp32A01","time":"2021-3-12 21:26:26",
     * "PH":0,"TDS":0,"Temperature":25.125}
     * */
    //存储数据
    public Map<String,Object> saveData(DeviceData device){
        Map<String,Object> map = null;

        //策略模式：
        //ConstantCheckMethod为检测策略，device数据集合，实现了CheckMethod的接口
        //只要别的检测策略实现了CheckMethod接口，就能很好替换当前的检测，提高了系统的扩展性
        map = CheckUtil.check(device,new ConstantCheckMethod());//检测单片机测量的数据是否异常
        //存储数据的key
        String deviceKey = RedisKeyUtil.getDeviceData(device.getDeviceId());
        //设备状态的key
        String deviceStatusKey = RedisKeyUtil.getDeviceStatus(device.getDeviceId());
        //尝试从redis中获取设备的信息
        Device tmp = (Device) redisTemplate.opsForValue().get(deviceStatusKey);



        //更新设备列表的状态

        //redis中没有缓存说明了该设备一开始是离线的，现在要将设备的设置为在线
        if(tmp==null){
            Device deviceStatus = deviceMapper.selectDeviceByDeviceId(device.getDeviceId());//从数据获取设备的一些基本信息
            deviceStatus.setStatus(1);
            redisTemplate.opsForValue().set(deviceStatusKey,deviceStatus,REPAIR_DEVICEDATA_SECONDS,TimeUnit.SECONDS);
        }else if(map.get("phMsg")!=null||map.get("tdsMsg")!=null||map.get("temperatureMsg")!=null){
            //修改储存在redis中设备（device）中状态，判断为故障。
            tmp.setStatus(2);
            redisTemplate.opsForValue().set(deviceStatusKey,tmp,REPAIR_DEVICEDATA_SECONDS, TimeUnit.SECONDS);

            //记录设备异常
            String deviceWarnKey = RedisKeyUtil.getDeviceWarning(device.getDeviceId());
            Integer a = (Integer)redisTemplate.opsForValue().get(deviceWarnKey);
            //当一次设备出现异常，需要发送电子邮件提醒设备管理者
            //在一定的维修时间内，设备的异常数据不会再次发送警告
            if(a == null) {
                //从数据库中获取设备管理者
                User user = userMapper.selelctUserById(tmp.getUserId());
                //创建邮件的内容的对象
                Context context = new Context();
                //填充电子邮件模块（templates/mail/warning.html）中email参数
                context.setVariable("email", user.getEmail());
                //将错误信息串起来
                StringBuilder sb = new StringBuilder();
                if(map.get("phMsg") != null) sb.append(map.get("phMsg")).append(",");
                if(map.get("tdsMsg") != null) sb.append(map.get("tdsMsg")).append(",");
                if(map.get("temperatureMsg") != null) sb.append(map.get("temperatureMsg")).append(",");

                //同上
                context.setVariable("Msg",sb.toString());
                context.setVariable("deviceId",tmp.getDeviceId());
                //使用模板引擎将上述的参数注入到模板中
                String text = templateEngine.process("/mail/warnings", context);

                //发送电子邮件
                mailClient.sendMail(text, "warning", user.getEmail());
                redisTemplate.opsForValue().set(deviceWarnKey,1,REPAIR_DEVICE_SECONDS,TimeUnit.SECONDS);
            }
        }else {
            //刷新设备状态时间
            tmp.setStatus(1);
            redisTemplate.opsForValue().set(deviceStatusKey,tmp,REPAIR_DEVICEDATA_SECONDS, TimeUnit.SECONDS);
        }


        //将最新的数据缓存到redis中
        redisTemplate.opsForValue().set(deviceKey,device,REPAIR_DEVICEDATA_SECONDS, TimeUnit.SECONDS);
        //同步插入MySQL中，进行持久化
        deviceDataMapper.insertDeviceData(device);

        return map;
    }

    //获取对应设备列表
    public List<Device> getDevicesList(){
        User user = hostHolder.getUser();//先登录才能查看设备列表
        if(user==null) return null;
        //从数据库中获取该用户的设备列表
        List<Device> list = deviceMapper.selectDevicesByUserId(user.getId());
        //如果是空的，就说明了该用户没有任何设备可以管理。
        if(list == null || list.isEmpty()) return list;
        //对该用户的设备列表的状态，进行修改。
        for(Device device:list){
            //获取设备最新的状态的RedisKey
            String deviceStatusKey = RedisKeyUtil.getDeviceStatus(device.getDeviceId());
            //查看缓存中设备的状态
            Device tmp = (Device)redisTemplate.opsForValue().get(deviceStatusKey);
            //如果缓存中有该设备，就使用缓存中状态来更新设备状态。
            if(tmp!=null) device.setStatus(tmp.getStatus());//更新当前设备的最新状态
        }
        return list;
    }

    public Map<String,Object> addDevice(Device device){
        Map<String,Object> map = new HashMap<>();
        Device tmp = deviceMapper.selectDeviceByDeviceId(device.getDeviceId());
        if(tmp == null){
            map.put("code",1);
            map.put("msg","设备不存在");
        }
        else if (!tmp.getActivationCode().equals(device.getActivationCode())) {
            map.put("code",2);
            map.put("msg","激活码不正确");
        }else{
            deviceMapper.updateDeviceById(tmp.getId(),device.getUserId(),
                    device.getPurpose(),device.getOther(),device.getLatitude(),device.getLongitude());
            map.put("code",0);
            map.put("msg","添加成功");
        }
        return map;
    }

    public Map<String,Object> changeDevice(Device device){
        Map<String,Object> map = new HashMap<>();
        Device tmp = deviceMapper.selectDeviceByDeviceId(device.getDeviceId());
        if(tmp == null){
            map.put("code",1);
            map.put("msg","设备不存在");
        }
       else{
            deviceMapper.changeDeviceById(tmp.getId(),device.getPurpose(),
                    device.getOther(),device.getLatitude(),device.getLongitude());
            map.put("code",0);
            map.put("msg","修改成功");
        }
        return map;
    }






    //获取设备的历史数据
    //这个方法有点失误，但是前端调试好，赖得去重写这，其中的逻辑
    //简单来说，不需要将这些参数分开，只需将数据库获取的数据列表直接返回即可。
    public List<Object> getDevicesDataHistory(String deviceId){
        User user = hostHolder.getUser();//先登录才能查看设备列表
        if(user==null) return null;

        //从数据库中获取设备的历史数据，从而绘制成折线图。
        List<DeviceData> datas = deviceDataMapper.selelctDatasByDeviceId(deviceId);


        //lists用于存储多个参数的历史数据。
        List<Object> lists = new ArrayList<>();

        //存储多个参数的历史数据的列表
        List<Float> ph = new ArrayList<>();
        List<Float> tds = new ArrayList<>();
        List<Float> temperature = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        int len = datas.size();

        for(int i=len-1;i>=0;i--){
            ph.add(datas.get(i).getPh());
            tds.add(datas.get(i).getTds());
            temperature.add(datas.get(i).getTemperature());
            dates.add(datas.get(i).getDeviceTime());
        }

        lists.add(ph);
        lists.add(tds);
        lists.add(temperature);
        lists.add(dates);
        return lists;
    }


    public List<DeviceData>getMapData(int userId){
        //将该用户的所有设备的实时数据存储在datas，从而注入前端显示
        List<DeviceData> datas = new ArrayList<>();
        //获取该用户拥有的设备
        List<Device> deviceList = deviceMapper.selectDevicesByUserId(userId);
        String deviceDataKey = null;
        for(Device device:deviceList){
            deviceDataKey = RedisKeyUtil.getDeviceData(device.getDeviceId());
            //从Redis中获取最新的数据
            DeviceData data = (DeviceData) redisTemplate.opsForValue().get(deviceDataKey);
            datas.add(data);
        }
        return datas;
    }





}
