package com.whllow.iot.util;

import javax.servlet.http.Cookie;

public class CookieUtil {

    public static String getValue(Cookie[] cookies, String key){
        if(cookies ==null) return null;
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(key))
                return cookie.getValue();
        }
        return null;
    }




}
