package com.whllow.iot;

import com.alibaba.fastjson.JSONObject;
import com.whllow.iot.entity.Device;
import com.whllow.iot.util.RedisKeyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class IotApplicationTests {

	@Autowired
	RedisTemplate<String,Object> redisTemplate;

	@Test
	void contextLoadsADD() {
		String deviceStatusKey = RedisKeyUtil.getDeviceStatus("Esp32A004");
		redisTemplate.opsForValue().set(deviceStatusKey,0,10, TimeUnit.SECONDS);

	}
	@Test
	void contextLoads() {
		String deviceStatusKey = RedisKeyUtil.getDeviceStatus("Esp32A004");
		Device de = (Device)redisTemplate.opsForValue().get(deviceStatusKey);
		System.out.println(de==null?777:de.getStatus());
	}
	@Test
	void jsonTest() {
		String data = "{\"payload\":{\"deviceId\":\"Esp32A004\",\"deviceTime\":\"2021-4-12 19:59:37\",\"ph\":0,\"tds\":0,\"temperature\":29.6875}, \"seq\":21318349, \"timestamp\":1618299185, \"topic\":\"TLVWVUDCK6/Esp32A01/data\", \"devicename\":\"Esp32A01\", \"productid\":\"TLVWVUDCK6\"}";
		JSONObject outJson = JSONObject.parseObject(data);
		JSONObject jsonObject = (JSONObject) outJson.get("payload");
		System.out.println(	jsonObject.getFloat("temperature"));
	}

}
