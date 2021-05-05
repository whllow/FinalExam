package com.whllow.iot;

import com.alibaba.fastjson.JSONObject;
import com.whllow.iot.controller.DeviceController;
import com.whllow.iot.entity.Device;
import com.whllow.iot.util.RedisKeyUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class IotApplicationTests {

	@Autowired
	RedisTemplate<String,Object> redisTemplate;

	@Autowired
	SimpleDateFormat simpleDateFormat;

	private static final Logger logger = LoggerFactory.getLogger(IotApplicationTests.class);


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

	@Test
	void LoggerTest() {
		String d = "2021-4-20 9:47:21";
		Date p= null;
		try {
			p = simpleDateFormat.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(p);
	}

}
