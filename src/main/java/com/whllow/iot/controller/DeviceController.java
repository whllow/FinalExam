package com.whllow.iot.controller;

import com.alibaba.fastjson.JSONObject;
import com.whllow.iot.entity.Device;
import com.whllow.iot.entity.DeviceData;
import com.whllow.iot.entity.IotConstance;
import com.whllow.iot.entity.TestData;
import com.whllow.iot.service.DeviceService;
import com.whllow.iot.util.HostHolder;
import com.whllow.iot.util.IotUntil;
import com.whllow.iot.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class DeviceController implements IotConstance {


    @Autowired
    public DeviceService deviceService;

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public HostHolder hostHolder;



    /*
    *
    *获取设备列表的页面
    * */
    @RequestMapping(path = "/deviceListPage/{userId}",method = RequestMethod.GET)
    public String getdeviceListPage(@PathVariable("userId")int uesrId){
        return "/site/deviceList";//获取site包下deviceList.html
    }

    //给设备列表注入数据
    @RequestMapping(path = "/deviceListData/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String getdeviceList(@PathVariable("userId")int uesrId){
        //通过UserID来获取设备列表(device表)，看一下设备的状态，用途等。
        List<Device> lists = deviceService.getDevicesList();
        String str = null;
        //在deviceList.html中var int=self.setInterval("clock()",5000)，
        // 设置了定时器，每5秒访问这个网址获取数据
        // 将设备列表转换为JSOn的格式，
        // 主要是Ajax的异步访问接收的数据格式是JSON，所以我将list转换为JSON
        str = IotUntil.getJSONString(Collections.singletonList(lists));
        return str;
    }



    /*
    *
    *获取实时数据
    *
    * */

    @RequestMapping(path = "/getLatestPage/{deviceId}",method = RequestMethod.GET)
    public String getLatest(@PathVariable("deviceId")String deviceId, ModelAndView model){
        model.addObject("deviceId",deviceId);//将设备名称注入网页
        return "/site/Latest";
    }


    /*
    * 获取最新的数据
    * */
    @RequestMapping(path = "/getLatestData/{deviceId}",method = RequestMethod.GET)
    @ResponseBody
    public String latestData(@PathVariable("deviceId")String deviceId){
        //因为使用Redis缓存了设备最新的数据，避免了每次都访问mysql，减少了数据库的压力和提高了访问速度
        String deviceKey = RedisKeyUtil.getDeviceData(deviceId);
        DeviceData data = (DeviceData) redisTemplate.opsForValue().get(deviceKey);
        float ph=0.0f,tds=0.0f,temper=0.0f;
        Date date = null;
        //判断设备是否在线，如果设备在线，这个没有任何作用，
        //如果设备离线时间过长，通过redis的设置的有效时间，会主动删除该键值，导致data为空值
        if(data!=null)
        {
            ph = data.getPh();
            tds = data.getTds();
            date = data.getDeviceTime();
            temper = data.getTemperature();
        }
        Map<String,Object> map = new HashMap<>();
        //hashMap中键值是前端获取数据的键值。
        map.put("ph",ph);
        map.put("tds",tds);
        map.put("temperature",temper);
        map.put("time",date);

        //获取实时数据，通过使用Ajax异步加载网页，这边只能通过JSON字符串来更新。
        String str = IotUntil.getJSONString(200,"success",map);
        return str;
    }


    /**
     *
     * 获取折现图
     *
     * */


    @RequestMapping(path="/getHistroyPage/{deviceId}")
    public String getgetHistroyPage(@PathVariable("deviceId")String deviceId,Model model){
        model.addAttribute("deviceId",deviceId);//将设备名称注入网页
        return "/site/line";
    }



    @RequestMapping(path = "/getHistroyData/{deviceId}",method = RequestMethod.GET)
    @ResponseBody
    public String getHistroyData(@PathVariable("deviceId") String deviceId){
        List<Object> lists = deviceService.getDevicesDataHistory(deviceId);//获取历史数据10

        return IotUntil.getJSONString(lists);//同上
    }


    /*
    *  地图信息
    * */
    @RequestMapping(path = "/map",method = RequestMethod.GET)
    public String getMapPage(Model model)
    {
        //将设备的基本信息查询出来（设备名，经纬度）
        List<Device> devicesList =  deviceService.getDevicesList();
        //数据注入
        model.addAttribute("deviceList",devicesList);
        return "/site/Amap2";
    }

    @RequestMapping(path = "/getMapData/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String getMapData(@PathVariable("userId")int userId)
    {
        //获取用户的各个设备的最新数据
        List<DeviceData> datas = deviceService.getMapData(userId);
        //同上，getJSONString这个方法是将对象转换为JSON字符串
        return IotUntil.getJSONString(Collections.singletonList(datas));
    }



    /**
     *   存储数据
     * */

    @RequestMapping(path = "/buildLatestData",method = RequestMethod.POST)
    @ResponseBody
    public String getData(DeviceData device,@RequestBody String data){
        //解析mqtt服务器转发的JSON数据。
        JSONObject outJson = JSONObject.parseObject(data);

        //存储数据到对象中
        DeviceData deviceData = new DeviceData();
        deviceData.setDeviceId(outJson.getString("deviceId"));
        deviceData.setPh(outJson.getFloat("ph"));
        deviceData.setTds(outJson.getFloat("tds"));
        deviceData.setTemperature(outJson.getFloat("temperature"));
        deviceData.setDeviceTime(outJson.getDate("deviceTime"));
        System.out.println(deviceData.toString());
        //将数据存入数据库中。
        Map<String,Object> map = deviceService.saveData(deviceData);

        return "ok";
    }





}
