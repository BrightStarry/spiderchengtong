package com.zx.executor;

import com.zx.task.GetIPTask;
import com.zx.task.SpiderTask;
import com.zx.util.ConfigUtil;
import com.zx.util.RedisUtil;
import com.zx.util.ThreadExceptionHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Main() {
        //加载配置
        ConfigUtil.initConfig();
        //准备
        SpiderTask.setup();
        this.spiderExecutor = Executors.newFixedThreadPool(ConfigUtil.THREAD_NUMBER);
    }



    /**
     * 开启ip定时获取任务
     */
    public void scheduleGetIp(){
        //指定线程工厂
        this.getIPExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            //在线程工厂中创建线程，并指定每个线程的异常处理方法
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setUncaughtExceptionHandler(new ThreadExceptionHandler());
                return thread;
            }
        });
        //执行ip获取任务
        GetIPTask getIPTask = new GetIPTask("ip获取任务", ConfigUtil.IP_COUNT.incrementAndGet());//这个runnable最终会被ThreadFactory的newThread方法再包裹一次
        getIPExecutor.scheduleAtFixedRate(getIPTask, 0, ConfigUtil.GET_IP_INTERVAL, TimeUnit.SECONDS);

    }

    /**
     * 任务运行
     */
    public void start() throws Exception {
            //如果是主进程则开启定时获取ip任务
            if(ConfigUtil.IS_MASTER){
                scheduleGetIp();
            }
            //线程数
            final int  threadNumber= ConfigUtil.THREAD_NUMBER;
            //运行程序数
            final AtomicInteger runingCount = ConfigUtil.RUNING_COUNT;
            //运行线程总数
            final AtomicInteger count  = ConfigUtil.COUNT;
            //循环执行主任务
            while (ConfigUtil.IS_RUN) {
                //如果正在运行的任务小于标准数，则新建任务，否则睡眠5s
                if (runingCount.get() < threadNumber) {
                    //每次线程数少于标准线程数，则 增加数目 = 标准数 - 当前数
                    while (runingCount.get() < threadNumber) {
                        /**
                         * 获取ip
                         */
                        //从redis获取ip
                        String ipAndPort = RedisUtil.blockGetValueByList(ConfigUtil.IP_QUEUE_NAME);
                        //如果提取到的为空，跳出本次循环
                        if(StringUtils.isBlank(ipAndPort))
                            continue;
                        //成功获取，则分割ip和poet
                        String[] strs = ipAndPort.split(":");
                        //执行主任务
                        SpiderTask spiderTask = new SpiderTask("主任务", count.incrementAndGet(), strs[0], Integer.valueOf(strs[1]));
                        spiderExecutor.execute(spiderTask);
                        //运行线程数+1
                        runingCount.incrementAndGet();
                    }
                }
                Thread.sleep(3000);
            }

    }

    /**
     * 异常处理
     */
    public static void restart() throws Exception {
        LOGGER.info("正在处理该未捕获异常");
        //先停止运行程序
        ConfigUtil.IS_RUN = false;
        //然后等待所有子任务结束
        while (ConfigUtil.RUNING_COUNT.get() != 0){
            LOGGER.info("正在等待所有子任务结束。。。");
            Thread.sleep(3000);
        }
        LOGGER.info("所有子任务已经结束，重启main方法");
        //重新运行main方法
        Main.main(null);
    }

    public static void main(String[] args) {
        try {
            LOGGER.info("任务开始");
            Main main = new Main();
            main.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
