package com.whllow.iot;

import com.whllow.iot.entity.Device;
import com.whllow.iot.util.RedisKeyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

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
}
