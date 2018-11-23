package com.imooc.myo2o.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by 程 on 2017/10/18.
 * Token缓存
 */
public class TokenCache {
    //声明日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //声明一个前缀
    public static final String TOKEN_PREFIX = "token_";
    //声明一个静态的内存块(LRU算法)
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认数据加载实现,当调用get取值的时候,如果key没有对应的值,就调用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    //存
    public static void setkey(String key,String value){
        localCache.put(key,value);
    }

    //取
    public static String getKey(String key){
        String value = null;
        try{
            value = localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
    
    //清除单个缓存
    public static void cleanKey(String key){
    	localCache.invalidate(key);
    }
}
