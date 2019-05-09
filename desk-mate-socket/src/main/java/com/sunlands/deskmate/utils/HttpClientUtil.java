package com.sunlands.deskmate.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Configurable;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Configurable
public class HttpClientUtil {
    static String erweimapath = "/sunlands/pic/erweima/";

    static final int timeOut = 10 * 1000;

    private static CloseableHttpClient httpClient = null;

    private final static Object syncLock = new Object();

    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     *
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    private static void setPostParams(HttpPost httpost,
                                      Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void setPostParams1(HttpPost httpost,
                                      Map<String, String> params) {

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static Pair<Integer,String> post(String url, Map<String, String> headers, Map<String, String> params, String requestBody) throws IOException {
        long start = System.currentTimeMillis();

        if (requestBody == null) {
            requestBody = "";
        }

        if (params == null) {
            params = new HashMap<>();
        }
        if (params.isEmpty()) {
            params.put("t", String.valueOf(System.currentTimeMillis()));
        }

        Set<String> keySet = params.keySet();
        NameValuePair[] nvps = new NameValuePair[keySet.size()];
        int i = 0;
        for (String key : keySet) {
            nvps[i] = new BasicNameValuePair(key, params.get(key).toString());
            i++;
        }

        RequestBuilder requestBuilder = RequestBuilder.post();
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }


        HttpUriRequest httpUriRequest = requestBuilder
                .setUri(url)
                .addParameters(nvps)
                .setEntity(new StringEntity(requestBody, Charset.forName("utf-8")))
                .build();

        CloseableHttpResponse response = null;
        String result = "";
        Integer responseCode=null;
        try {
            response = getHttpClient(url).execute(httpUriRequest,
                    HttpClientContext.create());
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                responseCode = response.getStatusLine().getStatusCode();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
            }

            Pair<Integer,String> pair = Pair.of(responseCode,result);
            log.info("post {} responseCode={} cost: {}ms --> \n{}", new Object[]{url + "?" + toParam(params, "utf-8") + "&body=" + requestBody, responseCode,(System.currentTimeMillis() - start), result});
            return pair;
        } catch (Exception e) {
            log.error("post {} responseCode={} cost: {}ms --> \n{}", new Object[]{url + "?" + toParam(params, "utf-8") + "&body=" + requestBody,responseCode, (System.currentTimeMillis() - start), result});
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /**
     *
     * @param url
     * @param s
     * @param erweimapath 图片生成本地路径
     * @return
     * @throws IOException
     */
    public static String post(String url, String s,String erweimapath) throws IOException {
        long start = System.currentTimeMillis();
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostString(httppost, s);
        CloseableHttpResponse response = null;
        String result = "";
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    InputStream instreams = resEntity.getContent();
                    String uploadSysUrl = erweimapath;
                    File saveFile = new File(uploadSysUrl+start+".png");
                    // 判断这个文件（saveFile）是否存在
                    if (!saveFile.getParentFile().exists()) {
                        // 如果不存在就创建这个文件夹
                        saveFile.getParentFile().mkdirs();
                    }
                    saveToImgByInputStream(instreams, uploadSysUrl, start+".png");
                    result=start+".png";
                    EntityUtils.consume(resEntity);
                }
            }
            log.info("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + s, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
          e.printStackTrace();
            log.error("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + s, (System.currentTimeMillis() - start), result});
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* @param instreams 二进制流
  * @param imgPath 图片的保存路径
  * @param imgName 图片的名称
  * @return
  *      1：保存正常
  *      0：保存失败
  */
    public static int saveToImgByInputStream(InputStream instreams, String imgPath, String imgName){

        int stateInt = 1;
        if(instreams != null){
            try {
                File file=new File(imgPath+imgName);//可以是任何图片格式.jpg,.png等
                FileOutputStream fos=new FileOutputStream(file);

                byte[] b = new byte[1024];
                int nRead = 0;
                while ((nRead = instreams.read(b)) != -1) {
                    fos.write(b, 0, nRead);
                }
                fos.flush();
                fos.close();
            } catch (Exception e) {
                stateInt = 0;
                e.printStackTrace();
            } finally {
                try {
                    instreams.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return stateInt;
    }

    /**
     * post
     *
     * @param url
     * @return
     * @author SHANHY
     * @throws IOException
     * @create 2015年12月18日
     */
    public static String post(String url, Map<String, Object> params) throws IOException {
        long start = System.currentTimeMillis();
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        String str = "";
        String result = "";
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            Object[] key_arr = params.keySet().toArray();
            for (Object key : key_arr) {
                String val = String.valueOf(params.get(key));
                str += "&" + key + "=" + val;
            }

            str.replaceFirst("&", "");
            log.info("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + str, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
//          e.printStackTrace();
            log.error("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + str, (System.currentTimeMillis() - start), result});
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String postJSON(String url, Map<String, String> jsonBody) throws IOException {
        String jsonString = JSONObject.toJSONString(jsonBody);
        return post(url, jsonString, (Map<String, String>) null);
    }

    public static String post(String url, String jsonString, Map<String, String> headers) throws IOException {

        long start = System.currentTimeMillis();
        HttpPost httppost = new HttpPost(url);
        if (headers != null) {
            for (String headName: headers.keySet()) {
                httppost.addHeader(headName, headers.get(headName));
            }
        }
        config(httppost);

        httppost.setHeader("Accept", "application/json;charset=UTF-8");
        httppost.setHeader("Content-type", "application/json;charset=UTF-8");

        StringEntity stringEntity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        httppost.setEntity(stringEntity);

        CloseableHttpResponse response = null;
        String result = "";
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);

            log.info("post url: {}, params: {}, cost: {}ms, result: {}", url, jsonString, (System.currentTimeMillis() - start), result);

            return result;
        } catch (Exception e) {

            log.error("post url: {}, params: {}, cost: {}ms, result: {}", url, jsonString, (System.currentTimeMillis() - start), result);
            log.error("e", e);

            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String post(String url, Map<String, Object> params, Map<String, String> headers) throws IOException {
        long start = System.currentTimeMillis();
        HttpPost httppost = new HttpPost(url);
        if (headers != null) {
            for (String headName: headers.keySet()) {
                httppost.addHeader(headName, headers.get(headName));
            }
        }
        config(httppost);
        setPostParams(httppost, params);
        CloseableHttpResponse response = null;
        String str = "";
        String result = "";
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            Object[] key_arr = params.keySet().toArray();
            for (Object key : key_arr) {
                String val = params.get(key).toString();
                str += "&" + key + "=" + val;
            }

            str.replaceFirst("&", "");
            log.info("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + str, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
//          e.printStackTrace();
            log.error("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + str, (System.currentTimeMillis() - start), result});
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String post(String url, String s) throws IOException {
        long start = System.currentTimeMillis();
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        setPostString(httppost, s);
        CloseableHttpResponse response = null;
        String result = "";
        try {
            response = getHttpClient(url).execute(httppost,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            log.info("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + s, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
//          e.printStackTrace();
            log.error("post {} cost: {}ms --> \n{}", new Object[]{url + "?" + s, (System.currentTimeMillis() - start), result});
            throw e;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * GET请求URL获取内容
     *
     * @param url
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static String get(String url) {
        long start = System.currentTimeMillis();
        String result = "";
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            log.info("get {} cost: {}ms --> \n{}", new Object[]{url, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
            log.error("get {} cost: {}ms --> \n{}", new Object[]{url, (System.currentTimeMillis() - start), result});
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String get(String url,Map<String,String> params) {
        long start = System.currentTimeMillis();
        String result = "";
        CloseableHttpResponse response = null;
        try {
            if(!params.isEmpty()){
                url +=  "?"+toParam(params,"utf-8");
            }
            HttpGet httpget = new HttpGet(url);
            config(httpget);
            response = getHttpClient(url).execute(httpget,
                    HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            log.info("get {} cost: {}ms --> \n{}", new Object[]{url, (System.currentTimeMillis() - start), result});
            return result;
        } catch (Exception e) {
            log.error("get {} cost: {}ms --> \n{}", new Object[]{url, (System.currentTimeMillis() - start), result});
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String toParam(Map<String, String> params, String encode) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }
            if (encode == null) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            } else {
                stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode));
            }

        }
        return stringBuilder.toString();
    }

    private static void setPostString(HttpPost httpost, String s) {
        httpost.setEntity(new StringEntity(s, Charset.forName("UTF-8")));
    }


    public static void main(String[] args) {
        // URL列表数组
        String[] urisToGet = {
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",

                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497",
                "http://blog.csdn.net/catoop/article/details/38849497" };

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                HttpGet httpget = new HttpGet(urisToGet[i]);
                config(httpget);
                // 启动线程抓取
                executors
                        .execute(new GetRunnable(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }

    static class GetRunnable implements Runnable {
        private CountDownLatch countDownLatch;
        private String url;

        public GetRunnable(String url, CountDownLatch countDownLatch) {
            this.url = url;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                System.out.println(HttpClientUtil.get(url));
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
