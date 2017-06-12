package com.zx.util;


import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpClient对象工具类
 */
public class HttpClientUtil {
    //日志
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    //连接池
    private PoolingHttpClientConnectionManager pool = null;
    //cookieStore对象，全局唯一，就留个清理方法
    private final CookieStore cookieStore = new BasicCookieStore();
    //请求配置
    private RequestConfig requestConfig;
    //请求头，配置给HttpRequest对象（post和get）
    private Map<String, String> header;

    //连接池连接最大数
    private final Integer MAX_CONNECTION_NUM = 10;
    //最大路由，
    //这里route的概念可以理解为 运行环境机器 到 目标机器的一条线路。
    // 举例来说，我们使用HttpClient的实现来分别请求 www.baidu.com 的资源和 www.bing.com 的资源那么他就会产生两个route。
    //如果设置成200.那么就算上面的MAX_CONNECTION_NUM设置成9999，对同一个网站，也只会有200个可用连接
    private final Integer MAX_PER_ROUTE = 10;
    //握手超时时间
    private final Integer SOCKET_TIMEOUT = 5000;
    //连接请求超时时间
    private final Integer CONNECTION_REQUEST_TIMEOUT = 5000;
    //连接超时时间
    private final Integer CONNECTION_TIMEOUT = 5000;


    /**
     * 获取HttpClient
     */
    public CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                //设置默认的cookieStore
                .setDefaultCookieStore(cookieStore)
                //设置连接池
                .setConnectionManager(pool)
                //请求配置
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpClient;
    }

    /**
     * 设置默认请求头
     */
    public void setDefaultHeaders(HttpRequest request) {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            request.setHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 从response 中取出 html String
     * 如果没有访问成功，返回null
     */
    public String responseToString(CloseableHttpResponse response) throws IOException {
        if (isSuccess(response)) {
            String html = EntityUtils.toString(response.getEntity(), "UTF-8");
            return html;
        }
        return null;
    }

    /**
     * 校验是否请求成功
     */
    public boolean isSuccess(CloseableHttpResponse response) {
        if (null == response) {
            return false;
        }
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    /**
     * 发起get请求，返回 response
     */
    public CloseableHttpResponse sendGetRequestForResponse(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        setDefaultHeaders(httpget);
        CloseableHttpResponse response = null;
        response = getHttpClient().execute(httpget);
        /**
         * 如果失败，关闭连接
         */
        if (!isSuccess(response)) {
            httpget.abort();
            closeResponseAndIn(null,response);
        }
        return response;
    }


    /**
     * 发起get请求，返回 html string
     */
    public String sendGetRequestForHtml(String url) throws Exception {
        CloseableHttpResponse response = sendGetRequestForResponse(url);
        String html = responseToString(response);
        closeResponseAndIn(null, response);
        return html;
    }


    /**
     * 关闭  in 和 response
     */
    public void closeResponseAndIn(InputStream inputStream, CloseableHttpResponse response) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                inputStream = null;
            }
        }
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response = null;
            }
        }
    }


    /**
     * 静态内部类，实现单例模式
     */
    static class HttpClientUtilSingleton {
        public static final HttpClientUtil httpClientUtil = new HttpClientUtil();
    }

    /**
     * 私有化构造方法，构造时，创建对应的连接池实例
     * 使用连接池管理HttpClient可以提高性能
     */
    private HttpClientUtil() {
        try {
            /**
             * 初始化连接池
             */
            SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
            sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContextBuilder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", socketFactory)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();
            pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pool.setMaxTotal(MAX_CONNECTION_NUM);
            pool.setDefaultMaxPerRoute(MAX_PER_ROUTE);

            /**
             * 初始化请求配置
             */
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    //cookie策略
                    .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .build();
            /**
             * 初始化请求头
             */
            header = new HashMap<>();
            header.put("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/50.0.2661.102 UBrowser/6.1.2107.204 Safari/537.36");
        } catch (Exception e) {
            LOGGER.error("HttpClientPool init failed (httpClient连接池创建失败)!");
        }
    }

    /**
     * 获取单例实例
     */
    public static HttpClientUtil getInstance() {
        return HttpClientUtilSingleton.httpClientUtil;
    }

    /**
     * 清空cookieStore
     */
    public void clearCookieStore() {
        cookieStore.clear();
    }
}
