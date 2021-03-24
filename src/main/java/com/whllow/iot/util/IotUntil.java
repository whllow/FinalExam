package com.whllow.iot.util;




import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public  class IotUntil {

    public static String buildUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    public static String MD5(String str){
        if(StringUtils.isBlank(str))
            return null;
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null){
            Set<String> keys = map.keySet();
            for(String key:keys){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }

    public static String getJSONString(List<Object> list){
        JSONArray data = new JSONArray(list);
        return data.toJSONString();
    }

}
