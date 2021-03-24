package com.whllow.iot.service;

import com.whllow.iot.dao.UserMapper;
import com.whllow.iot.entity.IotConstance;
import com.whllow.iot.entity.LoginTicket;
import com.whllow.iot.entity.User;
import com.whllow.iot.util.CookieUtil;
import com.whllow.iot.util.IotUntil;
import com.whllow.iot.util.MailClient;
import com.whllow.iot.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements IotConstance {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public MailClient mailClient;

    @Autowired
    public TemplateEngine templateEngine;

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;


    @Value("${iot.path.domain}")
    public String domain;

    @Value("${server.servlet.context-path}")
    public String contextPath;

    public User findUserById(int id){
        return userMapper.selelctUserById(id);
    }

    public User findUserByUsername(String username){
        return userMapper.selectUserByUsername(username);
    }

    public User findUserByEmail(String email){
        return userMapper.selectUserByEmail(email);
    }

    public int activation(int userid,String code){
        User user = userMapper.selelctUserById(userid);
        if(user.getStatus() == 1){
            return IotConstance.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userid,1);
            return IotConstance.ACTIVATION_SUCCESS;
        }else{
            return IotConstance.ACTIVATION_FAILURE;
        }
    }

    public int uploadHeader(int userId, String headerUrl) {
        int rows =  userMapper.updateHeader(userId, headerUrl);
        return rows;
    }

    public int changePassword(int userId,String password){
        User user = userMapper.selelctUserById(userId);
        String salt = user.getSalt();
        return userMapper.updatePassword(userId, IotUntil.MD5(password + salt));
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        System.out.println(user.getUsername());
        User tmp = userMapper.selectUserByUsername(user.getUsername());
        if(tmp != null){
            map.put("usernameMsg","用户已存在");
            return map;
        }
        tmp = userMapper.selectUserByEmail(user.getEmail());
        if(tmp != null){
            map.put("emailMsg","邮箱已被注册过");
            return map;
        }
        String salt = IotUntil.buildUUID().substring(0, 5);
        String password = IotUntil.MD5(user.getPassword() + salt);
        user.setSalt(salt);
        user.setPassword(password);
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setActivationCode(IotUntil.buildUUID());
        user.setCreateTime(new Date());

        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String text = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(text,"activation",user.getEmail());
        return map;
    }

    public Map<String,Object> login(User user,int time){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User tmp = userMapper.selectUserByUsername(user.getUsername());
        if(tmp == null){
            map.put("usernameMsg","该用户不存在");
            return map;
        }
        if(tmp.getStatus() == 0){
            map.put("usernameMsg","该用户未激活");
            return map;
        }
        String password = IotUntil.MD5(user.getPassword() + tmp.getSalt());
        if(!tmp.getPassword().equals(password)){
            map.put("passwordMsg","密码出错");
            return map;
        }

        long d = (new Date()).getTime() + time;


        //生成凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(tmp.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpire(new Date(d));
        loginTicket.setTicket(IotUntil.buildUUID());

        String ticketKey = RedisKeyUtil.getTicket(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket,time,TimeUnit.SECONDS);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    public Map<String,Object> logout(String ticket){
        Map<String,Object> map = new HashMap<>();
        String ticketKey = RedisKeyUtil.getTicket(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        return map;
    }

    public void forgetCode(String email,String code){
        User user = userMapper.selectUserByEmail(email);
        if(user == null) return;
        String passwordKey = RedisKeyUtil.getRreshPassword(user.getUsername());
        redisTemplate.opsForValue().set(passwordKey,code,5*60,TimeUnit.SECONDS);
        Context context = new Context();
        context.setVariable("email",email);
        context.setVariable("code",code);
        String text = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(text,"activation",email);
    }

    public Map<String, Object> forgerPassword(String email, String password, String code) {

        Map<String,Object> map = new HashMap<>();

        if (StringUtils.isBlank(email)) {
            map.put("emailMsg","邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(code)) {
            map.put("codeMsg","验证码不能为空");
            return map;
        }

        User user = userMapper.selectUserByEmail(email);

        if(user == null){
            map.put("emailMsg","邮箱不存在");
        }
        String passwordKey = RedisKeyUtil.getRreshPassword(user.getUsername());
        if(!code.equals((String)redisTemplate.opsForValue().get(passwordKey)))
        {
            map.put("codeMsg","验证码出错");
            return map;
        }
        String newPassword = IotUntil.MD5(password + user.getSalt());
        userMapper.updatePassword(user.getId(),newPassword);

        return map;
    }
}
