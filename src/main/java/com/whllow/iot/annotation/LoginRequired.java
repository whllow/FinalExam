package com.whllow.iot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//这个是一个注解，简单的来说，它是一个注释，专门给JVM（java虚拟机看的），
//可以说是一种标记，我们可以通过反射来获取哪些方法（ElementType.METHOD，还有类的注解，
// 自己看源码）使用了这个注解，从而区分普通方法和标记过的方法

//说了这么多，如果不明白，那么就自己百度，简单来说。它的作用就是判断该用户是否登录到系统。
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
