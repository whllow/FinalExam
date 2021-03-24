package com.whllow.iot.controller;

import com.google.code.kaptcha.Producer;
import com.whllow.iot.entity.IotConstance;
import com.whllow.iot.entity.User;
import com.whllow.iot.service.UserService;
import com.whllow.iot.util.CookieUtil;
import com.whllow.iot.util.IotUntil;
import com.whllow.iot.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements IotConstance {

    @Autowired
    public UserService userService;

    @Autowired
    public RedisTemplate<String,Object> redisTemplate;

    @Autowired
    public Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    public String contextPath;




    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegister(){
        return "/site/register";
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String getRegister(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map.isEmpty()||map == null){
            model.addAttribute("msg","注册成功，稍后请到邮箱中激活");
            model.addAttribute("target","/site/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    //http://localhost:8081/community/activation/155/5dccbcf3ed4f4fdbb97d5f99a71cae16
    @RequestMapping(path = "/activation/{id}/{activation}",method = RequestMethod.GET)
    public String getActivation(@PathVariable("id") int userid, @PathVariable("activation") String activation, Model model){
        int code = userService.activation(userid,activation);
        if(code == IotConstance.ACTIVATION_SUCCESS){
            model.addAttribute("msg","您的账号已经激活成功,可以正常使用了!");
            model.addAttribute("target","/login");
        }else if(code == IotConstance.ACTIVATION_REPEAT){
            model.addAttribute("msg","您的账号已经激活成功,无需重复激活");
            model.addAttribute("target","/login");
        }else {
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/register");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletRequest request, HttpServletResponse response){

        System.out.println("hello");

        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        String kaptchaOnwer = IotUntil.buildUUID();
        Cookie cookie = new Cookie("kaptchaOnwer",kaptchaOnwer);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        System.out.println(text);

        String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOnwer);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            System.out.println("服务器出错-验证码："+e.getMessage());
        }
    }


    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLogin(){
        return "/site/login";
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String Login(User user, Model model, String code, boolean rememberme,
                        HttpServletResponse response, @CookieValue("kaptchaOnwer")String kaptchaOnwer){
        if(StringUtils.isBlank(code)){
            model.addAttribute("codeMsg","验证码不能为空!");
            return "/site/login";
        }
        if(!StringUtils.isBlank(kaptchaOnwer)) {
            String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOnwer);
            String verifycode = (String) redisTemplate.opsForValue().get(kaptchaKey);
            if (!code.equals(verifycode)) {
                model.addAttribute("codeMsg", "验证码出错");
                return "/site/login";
            }
        }

        int time = rememberme?IotConstance.REMEMBER_EXPIRED_SECONDS:IotConstance.DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(user,time);

        if(map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket",(String)map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(time);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
                model.addAttribute("userNameMsg",map.get("usernameMsg"));
                model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String Login(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "/site/login";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForget(){
        return "/site/forget";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(Model model,
            String email,String password,String code){
        Map<String,Object> map = userService.forgerPassword(email,password,code);
        if(map.isEmpty()||map == null){
            model.addAttribute("msg","密码修改成功");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("codeMsg",map.get("codeMsg"));
            return "/site/forget";
        }
    }

    @RequestMapping(path = "/forgetcode/{email}",method = RequestMethod.GET)
    public void forgetcode(@PathVariable("email") String email,HttpServletResponse response){
        String context = kaptchaProducer.createText();
        String key = "key";
        userService.forgetCode(email,context);
        try (OutputStream os = response.getOutputStream()) {
            os.write(key.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
