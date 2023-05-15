package com.grace.frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开放，公共访问的注解，只在继承自BizController的类方法中使用有效。标识对该方法的请求，不进行权限验证。
 * <p>
 * 注：使用该注解后需要重启服务器，注解才会生效。
 * </p>
 * 
 * @author yjc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicAccess {}
