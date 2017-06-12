package com.zx.task;

import com.zx.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务类基类
 */
public abstract class BaseTask implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTask.class);
    //任务名
    protected String taskName;
    //任务序号
    protected Integer number;
    //任务启动时间
    protected Long createTime;
    //任务结果
    protected boolean status;


    /**
     * 任务结束时执行的方法
     */
    protected void destroy(boolean status,String error){

        this.status = status;
        //输出任务名，是否执行成功，执行耗时
        if(status){
            int i = ConfigUtil.count_success.incrementAndGet();
            LOGGER.info(taskName + ":" + number +"-END-time：" + ((System.currentTimeMillis() - createTime)/1000) + "s-成功数："+ i + "-当前运行线程数：" + ConfigUtil.RUNING_COUNT.decrementAndGet());
        }else{
            int i = ConfigUtil.count_failed.incrementAndGet();
            LOGGER.warn(taskName +":" + number +"-END-time：" + ((System.currentTimeMillis() - createTime)/1000) + "s-失败数：" + i + "失败原因:" + error + "-当前运行线程数：" + ConfigUtil.RUNING_COUNT.decrementAndGet());
        }
    }

}
