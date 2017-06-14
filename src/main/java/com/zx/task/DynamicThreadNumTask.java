package com.zx.task;

import com.zx.util.ConfigUtil;
import com.zx.util.TimeUtil;

/**
 * 动态调整线程数任务
 */
public class DynamicThreadNumTask extends BaseTask{


    /**
     * 构造方法
     */
    public DynamicThreadNumTask(String taskName, int number) {
        this.createTime = System.currentTimeMillis();
        this.taskName = taskName;
        this.number = number;
    }

    @Override
    public void run() {
        try {
            ConfigUtil.THREAD_NUMBER = TimeUtil.getThreadNumber();
            LOGGER.info("动态改变线程数成功！当前线程数:{}",ConfigUtil.THREAD_NUMBER);
        } catch (Exception e) {
            LOGGER.warn("动态变更线程数任务失败！error:{}",e.getMessage());
        }
    }
}
