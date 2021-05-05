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
        //获取注册页面
        return "/site/register";
    }



    /**
     * 提交注册用户信息
     *
     * */
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String getRegister(Model model, User user){
        //map中存储注册过程中会出现的问题。
        //注册操作
        Map<String,Object> map = userService.register(user);
        //map中没有数据或者为空，说明了注册过程中没有出错。
        if(map.isEmpty()||map == null){
            model.addAttribute("msg","注册成功，稍后请到邮箱中激活");
            model.addAttribute("target","/site/login");
            return "/site/operate-result";
        }else{
            //注册失败了，将失败的原因注入到页面中
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    //http://localhost:8081/community/activation/155/5dccbcf3ed4f4fdbb97d5f99a71cae16，激活码的格式
    @RequestMapping(path = "/activation/{id}/{activation}",method = RequestMethod.GET)
    public String getActivation(@PathVariable("id") int userid, @PathVariable("activation") String activation, Model model){
        //验证激活码的状态。
        int code = userService.activation(userid,activation);
        //成功激活该账号
        if(code == IotConstance.ACTIVATION_SUCCESS){
            model.addAttribute("msg","您的账号已经激活成功,可以正常使用了!");
            model.addAttribute("target","/login");
        }else if(code == IotConstance.ACTIVATION_REPEAT){//该账号已经激活了，无需重新激活
            model.addAttribute("msg","您的账号已经激活成功,无需重复激活");
            model.addAttribute("target","/login");
        }else {
            //激活失败
            model.addAttribute("msg","激活失败");
            model.addAttribute("target","/register");
        }
        return "/site/operate-result";
    }


    /*
    *
    * 获取验证码
    *
    * */
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletRequest request, HttpServletResponse response){

        //kaptchaProducer：验证码生成器，生成5位的验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);//将文本转换为图片

        String kaptchaOnwer = IotUntil.buildUUID();//生成128位UUID（随机数）
        //将这个随机当做一个key（作为该验证码的拥有者的key）
        //将这个key通过Cookie传输到本地浏览器，当下次访问该网站的时候，会将Cookie传输到浏览中，
        // 从而完成验证码的校验
        Cookie cookie = new Cookie("kaptchaOnwer",kaptchaOnwer);
        //设置Cookie的相关参数
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        System.out.println(text);
        userService.kcode(text);
        //通过kaptchaOnwer（随机数）生成一个key，并将上述的验证码存储到redis中，提高访问效率
        String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOnwer);
        redisTemplate.opsForValue().set(kaptchaKey,text,60, TimeUnit.SECONDS);

        //通过IO输出流将图片输出前端
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
        return "/site/login";//返回登录界面
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String Login(User user, Model model, String code, boolean rememberme,
                        HttpServletResponse response, @CookieValue("kaptchaOnwer")String kaptchaOnwer){
        //判断验证码是否为空
        if(StringUtils.isBlank(code)){
            model.addAttribute("codeMsg","验证码不能为空!");
            return "/site/login";
        }
        //判断验证码是否正确
        if(!StringUtils.isBlank(kaptchaOnwer)) {
            String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOnwer);//从redis中获取验证码
            String verifycode = (String) redisTemplate.opsForValue().get(kaptchaKey);
            if (!code.equals(verifycode)) {
                model.addAttribute("codeMsg", "验证码出错");
                return "/site/login";
            }
        }
        //不用登录的时间，缓存用户的时间
        int time = rememberme?IotConstance.REMEMBER_EXPIRED_SECONDS:IotConstance.DEFAULT_EXPIRED_SECONDS;

        //map中存储了登录时出错的信息
        Map<String,Object> map = userService.login(user,time);

        //map中含有ticket，说明了登录成功了。
        if(map.containsKey("ticket")) {
            //将ticket对应的value，当做一个凭证（没有登出的的凭证）
            //并通过Cookie传输到浏览器，当下次访问时，将其凭证发送到服务端中
            //服务端通过之前设置的拦截器（Interceptor），将user注入所有页面
            Cookie cookie = new Cookie("ticket",(String)map.get("ticket"));
            cookie.setPath(contextPath);
            cookie.setMaxAge(time);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            //登录失败，将对应失败信息注入页面
                model.addAttribute("userNameMsg",map.get("usernameMsg"));
                model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String Login(@CookieValue("ticket")String ticket){
        userService.logout(ticket);//登出，清理凭证
        return "/site/login";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForget(){
        return "/site/forget";//访问忘记密码
    }

    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(Model model,
            String email,String password,String code){

        //map存储忘记密码的错误信息
        Map<String,Object> map = userService.forgerPassword(email,password,code);

        //map没有任何消息，说明成功修改密码
        if(map.isEmpty()||map == null){
            model.addAttribute("msg","密码修改成功");
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else{
            //修改失败，将失败的信息注入页面中
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("codeMsg",map.get("codeMsg"));
            return "/site/forget";
        }
    }


    /*
    *
    * 发送忘记密码的验证码
    * */
    @RequestMapping(path = "/forgetcode/{email}",method = RequestMethod.GET)
    @ResponseBody
    public String forgetcode(@PathVariable("email") String email){
        String context = kaptchaProducer.createText();
        userService.forgetCode(email,context);
        return "ok";
    }

}
