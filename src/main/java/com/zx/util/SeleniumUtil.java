package com.zx.util;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * selenium 工具类
 */
public class SeleniumUtil {

    /**
     * 使用指定ip和port，获取对应的Selenium
     */
    public static Selenium getSelenium(String ip, int port){
        //设置代理
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("network.proxy.type", 1);
//        profile.setPreference("network.proxy.http", ip);
//        profile.setPreference("network.proxy.http_port", port);
//        profile.setPreference("network.proxy.ssl", ip);
//        profile.setPreference("network.proxy.ssl_port", port);
//        //所有协议公用一种代理配置，如果单独配置，这项设置为false
//        profile.setPreference("network.proxy.share_proxy_settings", true);
//        WebDriver driver = new FirefoxDriver(profile);

        WebDriver driver = new FirefoxDriver();

        String baseUrl = "https://www.pipipan.com/fs/14231261-207420331";
        Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);

        return selenium;
    }

    /**
     * 执行爬虫方法
     */
    public static void start(Selenium selenium){
        selenium.open("/fs/14231261-207420331");
        selenium.click("css=em");
        selenium.waitForPageToLoad("30000");
        selenium.waitForPopUp("_blank", "30000");
        selenium.click("id=free_down_link");
        selenium.waitForPopUp("", "30000");
        selenium.waitForPopUp("_blank", "30000");
    }

    /**
     * 直接调用该方法，处理所有事情
     */
    public static void run(String ip, int port){
        Selenium selenium = null;
        try{
            selenium = SeleniumUtil.getSelenium(ip, port);
            SeleniumUtil.start(selenium);
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            if(null != selenium)
                selenium.close();
        }
    }










}
