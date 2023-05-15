package com.grace.frame.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 配置给静态工具方法使用的缓存注解
 * 
 * @author yjc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalCache {
	public long maximumSize() default LocalCacheUtil.UNSET_INT;// 缓存最大数量

	public long expireAfterWrite() default LocalCacheUtil.UNSET_INT;// 写过期时间

	public TimeUnit expireAfterWriteUnit() default TimeUnit.SECONDS;// 写过期时间单位

	public long expireAfterAccess() default LocalCacheUtil.UNSET_INT;// 读过期时间

	public TimeUnit expireAfterAccessUnit() default TimeUnit.SECONDS;// 写过期时间单位
}
