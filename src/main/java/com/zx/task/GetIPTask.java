package com.zx.task;

import com.zx.util.ConfigUtil;
import com.zx.util.HttpClientUtil;
import com.zx.util.RedisUtil;
import com.zx.util.ResolveUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 获取ip任务类
 */
public class GetIPTask extends BaseTask{

    private static final Logger LOGGER = LoggerFactory.getLogger(GetIPTask.class);


    /**
     * 构造方法
     */
    public GetIPTask(String taskName, int number){
        this.createTime = System.currentTimeMillis();
        this.taskName = taskName;
        this.number = number;
    }


    @Override
    public void run() {
        getAndSaveIP();
    }

    /**
     * 提取ip并存入redis
     */
    public void getAndSaveIP(){
        HttpClientUtil httpClientUtil = HttpClientUtil.getInstance();
        ResolveUtil resolveUtil = ResolveUtil.getInstance();
        try {
            //发送请求获取响应
            String html = httpClientUtil.sendGetRequestForHtml(ConfigUtil.IP_PATH);
            List<String> ipList = resolveUtil.getListByRegexp("\\d{2,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{2,5}", html);
            if(ipList.size() == 0){
                LOGGER.info("获取ip时，ip为空！");
                return;
            }
            //存入redis
            RedisUtil.saveListToList(ConfigUtil.IP_QUEUE_NAME,ipList);
            LOGGER.info("拉取最新IP成功");
        } catch (Exception e) {
            LOGGER.warn("获取ip，发送请求时失败！error:"+ e.getCause().getMessage());
        }
    }

    public static void main(String[] args) {
        //加载配置
        ConfigUtil.initConfig();
        new GetIPTask().getAndSaveIP();
    }

    public GetIPTask(){}
}
