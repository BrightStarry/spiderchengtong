package com.zx.task;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zx.util.ConfigUtil;
import com.zx.util.HtmlUnitUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

/**
 * 刷 下载数 的主线程
 */
public class SpiderTask extends BaseTask {

    private String ip;
    private int port;



    public SpiderTask(String taskName, Integer number, String ip, int port){
        this.number = number;
        this.createTime = System.currentTimeMillis();
        this.taskName = taskName;
        this.ip = ip;
        this.port =port;
    }


    @Override
    public void run() {

        spider(ip, port);
    }

    /**
     * 爬虫方法
     */
    private  void spider(String ip,int port) {
        //htmlUnit工具类
        final HtmlUnitUtil htmlUnitUtil = HtmlUnitUtil.getInstance();
        try(
                final WebClient webClient = htmlUnitUtil.getWebClient(number % 2 == 0 ? BrowserVersion.CHROME : BrowserVersion.EDGE,ip,port)
        ){
            //设置 等待一定时间，等待js加载
            htmlUnitUtil.setJS(webClient);

            LOGGER.info(taskName + ": " + number + " start! 使用ip:" + ip + "-当前运行线程数：" + ConfigUtil.RUNING_COUNT.get());

            //每个线程开启后随机停止0-3000ms，防止任务同时访问页面
            Thread.sleep((int)(Math.random() * 3000));

            //获取第一个页面
            final HtmlPage page = (HtmlPage) webClient.getPage(ConfigUtil.SPIDER_PATH);

            Thread.sleep(ConfigUtil.WAIT_TIME);

            //获取并点击第一个下载按钮
            final HtmlElement freeDownLink = page.getHtmlElementById("free_down_link");
            LOGGER.info(taskName + ": " + number + " 爬取到第一个页面，标题为：" + page.getTitleText());
            HtmlPage page2 = (HtmlPage)freeDownLink.click();

            Thread.sleep(ConfigUtil.WAIT_TIME);

            //获取并点击第二个下载按钮
            final HtmlElement freeDownLink2 = page2.getHtmlElementById("free_down_link");
            LOGGER.info(taskName + ": " + number + " 爬取到第二个页面，标题为：" + page2.getTitleText());

            freeDownLink2.click();
            Thread.sleep(ConfigUtil.WAIT_TIME);
//            InputStream inputStream = response.getWebResponse().getContentAsStream();
//            LOGGER.info(taskName + ": " + number + "开始下载！");
//            FileUtils.copyToFile(inputStream, new File("F:/zhengxing/download/" + number + ".rar"));
            this.status = true;
            destroy(status,null);
        }catch(Exception e){
            this.status = false;
            destroy(status,e.getMessage());
        }
    }


}
