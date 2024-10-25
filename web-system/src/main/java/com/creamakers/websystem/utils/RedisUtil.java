package com.creamakers.websystem.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class RedisUtil {
    private final static String myLock = "lock";
    private final static String PASS_THROUGH = "防穿透";

    public static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(12);
    private StringRedisTemplate stringRedisTemplate;
    public RedisUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    // 普通set方法
    public <ID> void setByTime(String prefixKey, ID id, Object data,Long time, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(prefixKey + id, JSONUtil.toJsonStr(data),time,timeUnit);
    }
    // 普通del方法

    public void deleteValue(String key) {
        stringRedisTemplate.delete(key);
    }
    // 普通set方法 + 时间
    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), timeout, unit);
    }

    // 普通set方法
    public void setValue(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));
    }
    // 普通get方法
    public String getValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    // jvm锁 + 普通防穿透击穿 + 过期时间
    public <RE,ID> RE queryWithPassThrough(String prefixKey, ID id, Class<RE> reClass, Long time, TimeUnit timeUnit, Function<ID,RE> function) {
        String key = prefixKey + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(json)) {
            if(json.equals(PASS_THROUGH)) {
                return null;
            }
            return JSONUtil.toBean(json,reClass);
        }
        RE param = null;
        synchronized (myLock.intern()) {
            String check = stringRedisTemplate.opsForValue().get(key);
            if(StrUtil.isBlank(check)) {
                param = function.apply(id);
                if(param != null) {
                    this.setByTime(prefixKey,id,param,time,timeUnit);
                }
            } else if(!check.equals(PASS_THROUGH)) {
                param = JSONUtil.toBean(check,reClass);
            }
        }
        if(param == null) {
            stringRedisTemplate.opsForValue().set(key, PASS_THROUGH);
        }
        return param;
    }
}
