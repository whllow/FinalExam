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
        return "/site/deviceList";
    }

    //给设备列表注入数据
    @RequestMapping(path = "/deviceListData/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String getdeviceList(@PathVariable("userId")int uesrId){
        List<Device> lists = deviceService.getDevicesList();
        String str = null;
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
        model.addObject("deviceId",deviceId);
        return "/site/Latest";
    }


    /*
    * 获取最新的数据
    * */
    @RequestMapping(path = "/getLatestData/{deviceId}",method = RequestMethod.GET)
    @ResponseBody
    public String latestData(@PathVariable("deviceId")String deviceId){
        //造data
        String deviceKey = RedisKeyUtil.getDeviceData(deviceId);
        DeviceData data = (DeviceData) redisTemplate.opsForValue().get(deviceKey);
        float ph=0.0f,tds=0.0f,temper=0.0f;
        Date date = null;
        if(data!=null)
        {
            ph = data.getPh();
            tds = data.getTds();
            date = data.getDeviceTime();
            temper = data.getTemperature();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("ph",ph);map.put("tds",tds);map.put("temperature",temper);map.put("time",date);
        String str = IotUntil.getJSONString(200,"success",map);
        return str;
    }



    @RequestMapping(path="/getHistroyPage/{deviceId}")
    public String getgetHistroyPage(@PathVariable("deviceId")String deviceId,Model model){
        model.addAttribute("deviceId",deviceId);
        return "/site/line";
    }

    @RequestMapping(path = "/getHistroyData/{deviceId}",method = RequestMethod.GET)
    @ResponseBody
    public String getHistroyData(@PathVariable("deviceId") String deviceId){
        List<Object> lists = deviceService.getDevicesDataHistory(deviceId);
        return IotUntil.getJSONString(lists);
    }


    /*
    *  地图信息
    * */
    @RequestMapping(path = "/map",method = RequestMethod.GET)
    public String getMapPage(Model model)
    {
        List<Device> devicesList =  deviceService.getDevicesList();
        model.addAttribute("deviceList",devicesList);
        return "/site/Amap2";
    }

    @RequestMapping(path = "/getMapData/{userId}",method = RequestMethod.GET)
    @ResponseBody
    public String getMapData(@PathVariable("userId")int userId)
    {
        List<DeviceData> datas = deviceService.getMapData(userId);

        return IotUntil.getJSONString(Collections.singletonList(datas));
    }



    /**
     *   存储数据
     * */

    @RequestMapping(path = "/buildLatestData",method = RequestMethod.POST)
    @ResponseBody
    public String getData(DeviceData device,@RequestBody String data){
        JSONObject outJson = JSONObject.parseObject(data);
        DeviceData deviceData = new DeviceData();
        deviceData.setDeviceId(outJson.getString("deviceId"));
        deviceData.setPh(outJson.getFloat("ph"));
        deviceData.setTds(outJson.getFloat("tds"));
        deviceData.setTemperature(outJson.getFloat("temperature"));
        deviceData.setDeviceTime(outJson.getDate("deviceTime"));
        System.out.println(deviceData.toString());
        Map<String,Object> map = deviceService.saveData(deviceData);

        return "ok";
    }





}
