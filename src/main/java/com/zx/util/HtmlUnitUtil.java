package com.zx.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.BrowserVersionFeatures;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.lang3.StringUtils;
import sun.security.krb5.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 使用htmlunit的工具类
 */
public class HtmlUnitUtil {


    /**
     * 获取webClient
     * 下次优化的时候可以直接构造一个WebClient连接池
     * 目前使用后，不再复用
     */
    public WebClient getWebClient(BrowserVersion browserVersion, String ip, int port) {
        WebClient webClient = null;
        synchronized (this) {
            //如果传入的ip或port为空，则不使用代理
            if (StringUtils.isBlank(ip) || port == 0) {
                webClient = new WebClient(browserVersion == null ? BrowserVersion.CHROME : browserVersion);
            } else {
                webClient = new WebClient(browserVersion == null ? BrowserVersion.CHROME : browserVersion, ip, port);
            }


            webClient.getOptions().setActiveXNative(false);//不启用flash
            webClient.getOptions().setCssEnabled(false);//css,有些网站为了反爬虫，可能有所措施
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//不抛出状态码不为200的异常
            webClient.getOptions().setThrowExceptionOnScriptError(false);//不抛出脚本异常
            webClient.getOptions().setRedirectEnabled(true);//启用重定向
            webClient.getOptions().setTimeout(ConfigUtil.TIMEOUT);//设置浏览器请求超时时间
            webClient.getCookieManager().setCookiesEnabled(true);//启用cookie管理


            return webClient;
        }
    }

    /**
     * 设置好加载ajax的相关属性
     */
    public void setJS(WebClient webClient) {
        synchronized (this) {
            webClient.getOptions().setJavaScriptEnabled(true);//启用js
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());//支持ajxa
            webClient.waitForBackgroundJavaScript(ConfigUtil.WAIT_TIME);//设置js后台执行等待时间
            webClient.setJavaScriptTimeout(ConfigUtil.WAIT_TIME);//js等待超时时间
        }
    }

    /**
     * 单例模式，静态内部类
     */
    static class HtmlUnitUtilSingleton {
        public static final HtmlUnitUtil htmlUnitUtil = new HtmlUnitUtil();
    }

    /**
     * 私有化构造方法
     */
    private HtmlUnitUtil() {
    }

    /**
     * 获取该类唯一实例
     */
    public static HtmlUnitUtil getInstance() {
        return HtmlUnitUtilSingleton.htmlUnitUtil;
    }
}
