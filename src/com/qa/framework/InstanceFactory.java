package com.qa.framework;

import com.library.common.ReflectHelper;
import com.qa.framework.bean.Global;
import com.qa.framework.classfinder.ClassScanner;
import com.qa.framework.classfinder.impl.DefaultClassScanner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实例工厂
 */
public class InstanceFactory {

    /**
     * 用于缓存对应的实例
     */
    private static final Map<String, Object> cache = new ConcurrentHashMap<String, Object>();


    /**
     * 获取 ClassScanner
     *
     * @return the class scanner
     */
    public static ClassScanner getClassScanner() {
        return getInstance("class_scanner", DefaultClassScanner.class);
    }

    public static Global getGlobal() {
        return getInstance("Global", Global.class);
    }


    /**
     * Gets instance.
     *
     * @param <T>              the type parameter
     * @param cacheKey         the cache key
     * @param defaultImplClass the default impl class
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(String cacheKey, Class<T> defaultImplClass) {
        // 若缓存中存在对应的实例，则返回该实例
        if (cache.containsKey(cacheKey)) {
            return (T) cache.get(cacheKey);
        }
        // 通过反射创建该实现类对应的实例
        T instance = ReflectHelper.newInstance(defaultImplClass.getName());
        // 若该实例不为空，则将其放入缓存
        if (instance != null) {
            cache.put(cacheKey, instance);
        }
        // 返回该实例
        return instance;
    }
}
