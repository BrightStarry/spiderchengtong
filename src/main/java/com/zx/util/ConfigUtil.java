package com.zx.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置工具类
 */
public class ConfigUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtil.class);

    //htmlUnit超时时间
    public static int TIMEOUT;
    //htmlUnit等待页面加载时间
    public static int WAIT_TIME;

    //主线程数
    public static volatile Integer THREAD_NUMBER;
    //获取ip间隔时间,s
    public static int GET_IP_INTERVAL;
    //ip提取路径
    public static String IP_PATH;
    //爬取的路径集合
    public static List<String> SPIDER_PATHS;
    //Redis中的key
    public static String IP_QUEUE_NAME;

    //Redis ip
    public static String REDIS_IP;
    //Redis port
    public static int REDIS_PORT;

    //是否是主进程，主进程则开启定时抓取ip任务
    public static boolean IS_MASTER;

    //主任务是否运行，持续开启新线程
    public static boolean IS_RUN = Boolean.TRUE;

    //是否开启动态线程数
    public static boolean IS_DYNAMIC_THREAD_NUMBER;

    //执行总数
    public static volatile AtomicInteger COUNT = new AtomicInteger(0);
    //成功总数
    public static volatile AtomicInteger COUNT_SUCCESS = new AtomicInteger(0);
    //失败总数
    public static volatile AtomicInteger COUNT_FAILED = new AtomicInteger(0);

    //当前正在执行的线程总数
    public static volatile AtomicInteger RUNING_COUNT = new AtomicInteger(0);


    /**
     * 获取属性并赋值，并打印
     */
    public static void initConfig() {
        Map<String, String> config = getProperties("config");
        if (StringUtils.isNotBlank(config.get("TIMEOUT"))) {
            TIMEOUT = Integer.valueOf(config.get("TIMEOUT"));
        }
        if (StringUtils.isNotBlank(config.get("WAIT_TIME"))) {
            WAIT_TIME = Integer.valueOf(config.get("WAIT_TIME"));
        }
        if (StringUtils.isNotBlank(config.get("THREAD_NUMBER"))) {
            THREAD_NUMBER = Integer.valueOf(config.get("THREAD_NUMBER"));
        }
        if (StringUtils.isNotBlank(config.get("GET_IP_INTERVAL"))) {
            GET_IP_INTERVAL = Integer.valueOf(config.get("GET_IP_INTERVAL"));
        }
        if (StringUtils.isNotBlank(config.get("IP_PATH"))) {
            IP_PATH = config.get("IP_PATH");
        }
        if (StringUtils.isNotBlank(config.get("IP_QUEUE_NAME"))) {
            IP_QUEUE_NAME = config.get("IP_QUEUE_NAME");
        }
        if (StringUtils.isNotBlank(config.get("SPIDER_PATH"))) {
            SPIDER_PATHS = Arrays.asList(config.get("SPIDER_PATH").split(";"));
        }
        if (StringUtils.isNotBlank(config.get("REDIS_IP"))) {
            REDIS_IP = config.get("REDIS_IP");
        }
        if (StringUtils.isNotBlank(config.get("REDIS_PORT"))) {
            REDIS_PORT = Integer.valueOf(config.get("REDIS_PORT"));
        }
        if (StringUtils.isNotBlank(config.get("IS_MASTER"))) {
            IS_MASTER = Boolean.valueOf(config.get("IS_MASTER"));
        }
        if (StringUtils.isNotBlank(config.get("IS_DYNAMIC_THREAD_NUMBER"))) {
            IS_DYNAMIC_THREAD_NUMBER = Boolean.valueOf(config.get("IS_DYNAMIC_THREAD_NUMBER"));
        }
        printConfig();
    }

    /**
     * 输出所有值
     */
    public static void printConfig(){
        LOGGER.info("TIMEOUT:" + TIMEOUT);
        LOGGER.info("WAIT_TIME:" + WAIT_TIME);
        LOGGER.info("THREAD_NUMBER:" + THREAD_NUMBER);
        LOGGER.info("GET_IP_INTERVAL:" + GET_IP_INTERVAL);
        LOGGER.info("IP_PATH:" + IP_PATH);
        LOGGER.info("IP_QUEUE_NAME:" + IP_QUEUE_NAME);
        LOGGER.info("SPIDER_PATHS:{},SIZE：{}",SPIDER_PATHS,SPIDER_PATHS.size());
        LOGGER.info("REDIS_IP:" + REDIS_IP);
        LOGGER.info("REDIS_PORT:" + REDIS_PORT);
        LOGGER.info("IS_MASTER:" + IS_MASTER);
        LOGGER.info("IS_DYNAMIC_THREAD_NUMBER:" + IS_DYNAMIC_THREAD_NUMBER);
    }


    /**
     * 读取配置文件
     */
    public static Map<String, String> getProperties(String fileName) {
        Properties properties = new Properties();
        Map<String, String> map = new HashMap<>();
        try {
            /**
             * 项目CLASSPATH目录
             */
//            final InputStream in = Properties.class.getResourceAsStream("/" + fileName + ".properties");//静态方法
//        final InputStream in = getClass().getResourceAsStream("test.properties"); //普通方法
            /**
             * 硬盘目录
             */
            File file = null;
            //判断系统类型
            if((System.getProperty("os.name").toLowerCase()).contains("linux")){
                file = new File(File.separator + "zhengxing" + File.separator + fileName + ".properties");
            }else{
                file = new File("D:/zhengxing/" + fileName + ".properties");
            }
            final InputStream in = new FileInputStream(file);
            properties.load(new InputStreamReader(in, "UTF-8"));
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                map.put(key, value);
            }
            return map;
        } catch (IOException e) {
            LOGGER.warn("读取配置文件错误！");
        }
        return map;
    }


}
