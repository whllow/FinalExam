package com.whllow.iot.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {//验证码的第三方库配置文件

    //spring中需要将类注册到IOC容器中，才能通过IOC容器获取该实例对象
    //但是这个类不是自己写的，所以不能通过@Autowired来自动装配
    //这里使用@Bean,类似工厂模式，将第三库中类通过它修饰的方法，注入到Spring的IOC容器中。
    @Bean
    public Producer kaptchaProducer(){
        //一个配置文件，用法和HashMap类似，都是Key——Value结构，本身这个类似txt，只是这个类封装了
        //比较多的方法，可以从中快速获取key对应的value。
        Properties properties =  new Properties();
        //设置Kaptcha（验证码）类相关的参数，英文直译不翻译
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textProducer.front.size","32");
        properties.setProperty("kaptch.textProducer.front.color","0,0,0");
        properties.setProperty("kaptch.textProducer.char.String","0123456789abcdefghijklnmopqrstuvwxyz");
        properties.setProperty("kaptch.textProducer.char.length","3");

        //验证码的遮掩的算法，这里采用google的第三库。
        properties.setProperty("kaptcha.noise.imp","com.google.code.kaptcha.impl.NONoise");


        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);//将配置文件注入Kaptcha类生成验证码
        return kaptcha;
    }


}
