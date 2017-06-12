package com.zx.test;

import com.zx.util.ConfigUtil;
import com.zx.util.RedisUtil;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 这个测试类主要需要解决的问题是：
 * 当开启一个定时的访问一个URL拉取ip的任务，例如间隔x秒，
 * 一旦x秒的间隔内，调用redis的blpop()方法，并且该list中的元素为空，导致这个方法被阻塞等待元素插入时，定时任务就失效了
 */
public class ScheduleTest {

    public static void main(String[] args) throws Exception {
//        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
//        //每10s执行一次该任务
//        ses.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("定时任务执行");
//            }
//        },0,3, TimeUnit.SECONDS);
//        while(true){
//            System.out.println("sleep");
//            Thread.sleep(5000);
//            System.out.println("wake");
//            RedisUtil.blockGetValueByList("ip");
//        }
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
        System.out.println(new Random().nextInt(3000) + 15000);
    }
}
