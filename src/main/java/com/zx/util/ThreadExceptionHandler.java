package com.zx.util;

import com.zx.executor.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程内未捕获异常处理类
 */
@Deprecated
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadExceptionHandler.class);
    //未捕获异常处理
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //如果发生未知异常，
        //打印异常信息
        LOGGER.error("！！！！！！！！！！！发生未捕获异常，error：" + e.getCause().getMessage());
        //执行异常处理方法
        try {
            Main.restart();
        } catch (Exception e1) {
            LOGGER.error("未捕获异常处理失败，程序终止！error:" + e1.getCause().getMessage());
        }
    }
}
