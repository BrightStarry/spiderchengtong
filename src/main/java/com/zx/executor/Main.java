package com.zx.executor;

import com.zx.task.GetIPTask;
import com.zx.task.SpiderTask;
import com.zx.util.ConfigUtil;
import com.zx.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * 执行器
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    //爬虫任务执行器
    private ExecutorService spiderExecutor;
    //ip任务执行器
    private ScheduledExecutorService getIPExecutor;

    /**
     * 构造 两个任务执行器
     */
    public Main() {
        //加载配置
        ConfigUtil.initConfig();
        //输出配置
        ConfigUtil.printConfig();
        this.spiderExecutor = Executors.newFixedThreadPool(ConfigUtil.THREAD_NUMBER);
        this.getIPExecutor = Executors.newScheduledThreadPool(1);
    }

    /**
     * 开启ip定时获取任务
     */
    public void scheduleGetIp(){
        //执行ip获取任务
        GetIPTask getIPTask = new GetIPTask("ip获取任务", ConfigUtil.ip_count.incrementAndGet());
        getIPExecutor.scheduleAtFixedRate(getIPTask, 0, ConfigUtil.GET_IP_INTERVAL, TimeUnit.SECONDS);

    }

    /**
     * 任务运行
     */
    public void start() {
        try {
            //如果是主进程则开启定时获取ip任务
            if(ConfigUtil.IS_MASTER){
                scheduleGetIp();
            }
            final int  threadNumber= ConfigUtil.THREAD_NUMBER;
            //循环执行主任务
            while (true) {
                //如果正在运行的任务小于标准数，则新建任务，否则睡眠5s
                if (ConfigUtil.RUNING_COUNT.get() < threadNumber) {
                    //每次线程数少于标准线程数，则 增加数目 = 标准数 - 当前数
                    while (ConfigUtil.RUNING_COUNT.get() < threadNumber) {
                        /**
                         * 获取ip
                         */
                        //从redis获取ip
                        String ipAndPort = RedisUtil.blockGetValueByList(ConfigUtil.IP_QUEUE_NAME);
                        //成功获取，则分割ip和poet
                        String[] strs = ipAndPort.split(":");
                        //执行主任务
                        SpiderTask spiderTask = new SpiderTask("主任务", ConfigUtil.count.incrementAndGet(), strs[0], Integer.valueOf(strs[1]));
                        spiderExecutor.execute(spiderTask);
                        //运行线程数+1
                        ConfigUtil.RUNING_COUNT.incrementAndGet();
                    }
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        LOGGER.info("任务开始-----------------");
        Main main = new Main();
        main.start();

    }
}
