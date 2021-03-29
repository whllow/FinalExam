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
        //通过id来获取用户的信息
        return userMapper.selelctUserById(id);
    }

    public User findUserByUsername(String username){
        //通过username来获取用户信息
        return userMapper.selectUserByUsername(username);
    }

    public User findUserByEmail(String email){
        //通过email来获取用户信息
        return userMapper.selectUserByEmail(email);
    }


    /*
    *
    * 激活新注册用户
    *
    * */
    public int activation(int userid,String code){
        User user = userMapper.selelctUserById(userid);//通过user的id来获取user的信息
        //user的Status == 1 ，表示已经激活了
        if(user.getStatus() == 1){
            return IotConstance.ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            //激活码和数据库中的激活码一致，就可以成功激活用户，将用户的状态设置为1，表示已激活
            userMapper.updateStatus(userid,1);
            return IotConstance.ACTIVATION_SUCCESS;
        }else{
            return IotConstance.ACTIVATION_FAILURE;
        }
    }

    public int uploadHeader(int userId, String headerUrl) {
        //更新用户的头像
        int rows =  userMapper.updateHeader(userId, headerUrl);
        return rows;
    }

    /**
     *
     * 修改密码
     *
     * */
    public int changePassword(int userId,String password){
        User user = userMapper.selelctUserById(userId);
        String salt = user.getSalt();
        //用MD5对自己设置的密码和随机数（salt）进行加密，提高密码的复杂度，防止别人暴力破解
        return userMapper.updatePassword(userId, IotUntil.MD5(password + salt));
    }

    /**
     *
     * 注册操作
     *
     * */
    public Map<String,Object> register(User user){

        //存储错误信息
        Map<String,Object> map = new HashMap<>();

        /*
        *
        * 空值判断
        *
        * */
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

        /*
        *
        * 检测用户是否已经存在
        *
        * */

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

        //完成对用户的检测，开始进行注册操作




        //salt 为随机数，主要是防止用户设置的密码过于简单，导致强行破解
        String salt = IotUntil.buildUUID().substring(0, 5);
        //MD5加密，该加密是不可逆的
        String password = IotUntil.MD5(user.getPassword() + salt);
        //存储用户的各种信息
        user.setSalt(salt);
        user.setPassword(password);
        user.setType(0);
        user.setStatus(0);
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setActivationCode(IotUntil.buildUUID());
        user.setCreateTime(new Date());

        //存储在数据库中
        userMapper.insertUser(user);

        //设置邮件的内容
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //激活码的网址
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        //通过模板引擎将数据注入模板中
        String text = templateEngine.process("/mail/activation",context);
        //发邮件
        mailClient.sendMail(text,"activation",user.getEmail());
        return map;
    }

    public Map<String,Object> login(User user,int time){
        Map<String,Object> map = new HashMap<>();

        /*
         *
         * 空值判断
         *
         * */

        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }





        //通过username来获取对应用户的基本信息
        User tmp = userMapper.selectUserByUsername(user.getUsername());
        if(tmp == null){
            map.put("usernameMsg","该用户不存在");
            return map;
        }
        if(tmp.getStatus() == 0){
            map.put("usernameMsg","该用户未激活");
            return map;
        }

        //验证密码是否正确，暗码存储，明码传输
        String password = IotUntil.MD5(user.getPassword() + tmp.getSalt());
        if(!tmp.getPassword().equals(password)){
            map.put("passwordMsg","密码出错");
            return map;
        }

        //凭证的有效期
        long d = (new Date()).getTime() + time;


        //生成凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(tmp.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpire(new Date(d));
        loginTicket.setTicket(IotUntil.buildUUID());

        String ticketKey = RedisKeyUtil.getTicket(loginTicket.getTicket());
        //将凭证缓存到redis数据库中
        redisTemplate.opsForValue().set(ticketKey,loginTicket,time,TimeUnit.SECONDS);
        //将凭证的RedisKey的部分，返回到Controller中，并通过Cookie传输到浏览器中，存储
        map.put("ticket", loginTicket.getTicket());

        return map;
    }


    /*
    *
    * 登出操作
    *
    * */

    public Map<String,Object> logout(String ticket){
        Map<String,Object> map = new HashMap<>();

        /*
        *
        * 理论上，登出就是将凭证删除，但是为了网络攻击，越过redis（缓存层），直接访问数据库导致数据库宕机
        * 这里不是直接将凭证删除，而是修改其状态码。
        *
        * */
        String ticketKey = RedisKeyUtil.getTicket(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
        return map;
    }

    public void forgetCode(String email,String code){
        //获取用户信息
        User user = userMapper.selectUserByEmail(email);
        if(user == null) return;
        //生成忘记密码的key
        String passwordKey = RedisKeyUtil.getRreshPassword(user.getUsername());
        //并将验证码缓存到Redis中，有效期为5分钟
        redisTemplate.opsForValue().set(passwordKey,code,5*60,TimeUnit.SECONDS);
        //发送邮件
        Context context = new Context();
        context.setVariable("email",email);//邮箱
        context.setVariable("code",code);//验证码
        String text = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(text,"activation",email);//发送邮件的方法
    }

    public Map<String, Object> forgerPassword(String email, String password, String code) {

        Map<String,Object> map = new HashMap<>();

        /*
         *
         * 空值判断
         *
         * */

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
        //获取用户基本信息
        User user = userMapper.selectUserByEmail(email);

        //各种验证

        if(user == null){
            map.put("emailMsg","邮箱不存在");
        }
        //从Redis中获取该用户的验证码
        String passwordKey = RedisKeyUtil.getRreshPassword(user.getUsername());
        //比对验证码
        if(!code.equals((String)redisTemplate.opsForValue().get(passwordKey)))
        {
            map.put("codeMsg","验证码出错");
            return map;
        }

        //生成暗码
        String newPassword = IotUntil.MD5(password + user.getSalt());
        userMapper.updatePassword(user.getId(),newPassword);//修改密码

        return map;
    }
}
