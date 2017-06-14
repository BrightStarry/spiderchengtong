/*
package com.zx.test;

import com.thoughtworks.selenium.Selenium;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.MarionetteDriverManager;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxProfile;

import static org.junit.Assert.*;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.join;

public class test {
	private Selenium selenium;

	@Before
	public void setUp() throws Exception {
		//获取浏览器驱动exe程序
		FirefoxDriverManager.getInstance().setup();
		//设置代理
		String proxyIp = "139.59.211.210";
		int proxyPort = 8080;
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("network.proxy.type", 1);
		profile.setPreference("network.proxy.http", proxyIp);
		profile.setPreference("network.proxy.http_port", proxyPort);
		profile.setPreference("network.proxy.ssl", proxyIp);
		profile.setPreference("network.proxy.ssl_port", proxyPort);
		//所有协议公用一种代理配置，如果单独配置，这项设置为false
		profile.setPreference("network.proxy.share_proxy_settings", true);


		WebDriver driver = new FirefoxDriver(profile);
		String baseUrl = "https://www.pipipan.com/fs/14231261-207420331";
		selenium = new WebDriverBackedSelenium(driver, baseUrl);
	}

	@Test
	public void testTest() throws Exception {
		selenium.open("/fs/14231261-207420331");
		selenium.click("css=em");
		selenium.waitForPageToLoad("30000");
		selenium.waitForPopUp("_blank", "30000");
		selenium.click("id=free_down_link");
		selenium.waitForPopUp("", "30000");
		selenium.waitForPopUp("_blank", "30000");
	}

	@After
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
*/
