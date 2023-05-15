package com.grace.frame.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;

/**
 * 缓存工具
 */
public class LocalCacheUtil{
	/**
	 * 未设置标注
	 */
	public static final int UNSET_INT = -1;

	/**
	 * 缓存对象
	 */
	private static ConcurrentHashMap<String, LoadingCache<DataMap, DataMap>> DATA_CACHE = new ConcurrentHashMap<String, LoadingCache<DataMap, DataMap>>();// 其他数据缓存

	/**
	 * 反射执行方法
	 * <p>
	 * 执行静态工具方法，工具方法必须出参、入参类型均为DataMap。并且方法上使用了LocalCache注解 <br>
	 * 事例代码： <br>
	 * 
	 * <pre>
	 * --->@LocalCache(expireAfterWrite = 30, expireAfterWriteUnit = TimeUnit.SECONDS)
	 * --->public DataMap getTestData(DataMap para) throws AppException {
	 * --->	System.out.println("LOAD-Start->:" + para.toJsonString());
	 * --->	Sql sql = new Sql();
	 * --->	sql.setSql(" select csz from fw_sys_para limit 1 ");
	 * --->	DataSet dsTemp = sql.executeQuery();
	 * --->	DataMap rdm = new DataMap();
	 * --->	rdm.put("data", dsTemp);
	 * --->	System.out.println("LOAD-End->:" + rdm.toJsonString());
	 * --->	return rdm;
	 * --->}
	 * </pre>
	 * </p>
	 * 
	 * @author yjc
	 * @throws ExecutionException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws AppException
	 * @date 创建时间 2020-8-5
	 * @since V1.0
	 */
	public static DataMap run(Class<?> classObj, String methodName,
			DataMap para, boolean reset) throws ExecutionException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, AppException {
		final Method method = classObj.getMethod(methodName, DataMap.class);
		if (!Modifier.isStatic(method.getModifiers())) {
			throw new AppException("当前执行方法不是静态方法，请按照：public static DataMap methodName(DataMap para)方式声明工具类方法");
		}
		if (!method.isAnnotationPresent(LocalCache.class)) {// 是否有缓存注解，如果没有直接去执行即可
			DataMap rdm = (DataMap) method.invoke(null, para);// 静态工具方法
			return rdm;
		}

		// 使用缓存
		StringBuffer keyBF = new StringBuffer();
		keyBF.append(classObj.getName()).append(".").append(methodName);
		String key = keyBF.toString();

		LoadingCache<DataMap, DataMap> cache = LocalCacheUtil.DATA_CACHE.get(key);
		if (null == cache) {
			// 获取注解
			LocalCache localCacheAnnotation = method.getAnnotation(LocalCache.class);

			// 数据加载
			CacheLoader<DataMap, DataMap> cacheLoader = new CacheLoader<DataMap, DataMap>(){
				public DataMap load(DataMap para) throws Exception {
					DataMap rdm = (DataMap) method.invoke(null, para);
					return rdm;
				}
			};

			// builder
			CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
			if (localCacheAnnotation.maximumSize() != LocalCacheUtil.UNSET_INT) {
				cacheBuilder.maximumSize(localCacheAnnotation.maximumSize());
			}
			if (localCacheAnnotation.expireAfterWrite() != LocalCacheUtil.UNSET_INT) {
				cacheBuilder.expireAfterWrite(localCacheAnnotation.expireAfterWrite(), localCacheAnnotation.expireAfterWriteUnit());
			}
			if (localCacheAnnotation.expireAfterAccess() != LocalCacheUtil.UNSET_INT) {
				cacheBuilder.expireAfterAccess(localCacheAnnotation.expireAfterAccess(), localCacheAnnotation.expireAfterAccessUnit());
			}

			// 创建缓存
			cache = cacheBuilder.build(cacheLoader);

			// 放入map
			LocalCacheUtil.DATA_CACHE.put(key, cache);
		} else {
			// 重置缓存
			if (reset) {
				cache.invalidate(para);
			}
		}
		return cache.get(para);
	}

	/**
	 * 使用缓存
	 * 
	 * @author yjc
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws AppException
	 * @date 创建时间 2020-8-5
	 * @since V1.0
	 */
	public static DataMap run(final Class<?> classObj, final String methodName,
			DataMap para) throws ExecutionException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, AppException {
		return LocalCacheUtil.run(classObj, methodName, para, false);
	}

	/**
	 * 缓存全部清空
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-5
	 * @since V1.0
	 */
	public static void clear() {
		Iterator<String> iterator = LocalCacheUtil.DATA_CACHE.keySet()
			.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			LoadingCache<DataMap, DataMap> cache = LocalCacheUtil.DATA_CACHE.get(key);
			cache.invalidateAll();
		}
	}

	/**
	 * 指定缓存清空
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-5
	 * @since V1.0
	 */
	public static void clear(Class<?> classObj, String methodName) {
		StringBuffer keyBF = new StringBuffer();
		keyBF.append(classObj.getName()).append(".").append(methodName);
		String key = keyBF.toString();
		LocalCacheUtil.clear(key);
	}

	/**
	 * 指定缓存清空
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-5
	 * @since V1.0
	 */
	public static void clear(String key) {
		LoadingCache<DataMap, DataMap> cache = LocalCacheUtil.DATA_CACHE.get(key);
		if (null != cache) {
			cache.invalidateAll();
		}
	}
}
