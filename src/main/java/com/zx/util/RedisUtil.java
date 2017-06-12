package com.zx.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * Created by 97038 on 2017-06-08.
 */
public class RedisUtil {

    private static final Logger LOGGER  = LoggerFactory.getLogger(RedisUtil.class);

    // 连接池
    private static JedisPool pool = null;

    static{
            // redis连接池配置类
            JedisPoolConfig config = new JedisPoolConfig();
            // 应该是最大连接数，百度上是maxActive，但没有这个方法
            // -1表示不限制
            config.setMaxTotal(2);
            // 一个pool最多有多少个状态为idle(空闲的)的实例
            config.setMaxIdle(2);
            config.setMinIdle(0);
            // 当borrow(引入)jedis连接时，超时时间，如果超时，抛出JedisConnectionException
            config.setMaxWaitMillis(10000);// 10s
            // 在borrow一个redis实例时，是否提前验证，如果为true，保证获取到的redis实例都是可用的
            config.setTestOnBorrow(true);
            pool = new JedisPool(config, ConfigUtil.REDIS_IP, ConfigUtil.REDIS_PORT);

    }

    /**
     * 获取连接
     */
    private static synchronized Jedis getJedis() {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
            } catch (Exception e) {
                LOGGER.debug("获取 jedis 时失败！error:" + e.getCause().getMessage());
            }
            return jedis;

    }

    /**
     * 将List<String> 所有元素存入list
     */
    public static void saveListToList(String key,List<String> ipList){
            try (Jedis jedis = getJedis()) {
                jedis.lpush(key, ipList.toArray(new String[ipList.size()]));
            } catch (Exception e) {
                LOGGER.debug("jedis,saveListToList()方法失败！" );
            }
    }

    /**
     * 从list中阻塞地获取元素
     */
    public static String blockGetValueByList(String key){
            try (Jedis jedis = getJedis()) {
                List<String> list = jedis.blpop(0, key);
                return list.get(1);
            } catch (Exception e) {
                LOGGER.debug("jedis,blockGetValueByList()方法失败!" );
            }
            return "";
    }
}
