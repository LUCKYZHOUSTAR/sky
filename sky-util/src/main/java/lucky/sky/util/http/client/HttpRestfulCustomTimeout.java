package lucky.sky.util.http.client;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRestfulCustomTimeout {
    private static final Logger logger = LoggerFactory
            .getLogger(HttpRestful.class);

    private static String getRunInternal(HttpGet httpGet,
                                         int socketTimeOutMillisecond, int connTimeoutMillisecond)
            throws URISyntaxException {
        long startTime = System.currentTimeMillis();
        try {
            HttpClientAgentUtils httpClient = new HttpClientAgentUtils();
            httpClient.setSocketTimeOut(socketTimeOutMillisecond);
            httpClient.setConnectionTimeOut(connTimeoutMillisecond);

            HttpClientAgentUtils.HttpResult result = httpClient.httpExecute(httpGet);
            String content = result.getContent();
            return content;
        } finally {

        }
    }

    public static String getRun(String inputJson, String url,
                                int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            String reqString = url;
            if (StringUtils.isNotBlank(inputJson)) {
                reqString = reqString + "?req="
                        + URLEncoder.encode(inputJson, "utf-8");
            }

            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(reqString));
            return getRunInternal(httpGet, socketTimeoutMillisecond,
                    connTimeoutMillisecond);

        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        }
    }


    public static String getRunWithProperty(Map keyValue, String url,
                                int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            String reqString = url;
            int i =0;
            if(keyValue!=null && keyValue.size()!=0){
                for (Object key : keyValue.keySet()) {

                    if(i==0){
                        reqString = reqString + "?"+key+"="
                        + URLEncoder.encode(String.valueOf(keyValue.get(key)), "utf-8");
                    }else{
                        reqString = reqString + "&"+key+"="
                                + URLEncoder.encode(String.valueOf(keyValue.get(key)), "utf-8");
                    }
                    i++;
                }

            }
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(reqString));
            return getRunInternal(httpGet, socketTimeoutMillisecond,
                    connTimeoutMillisecond);

        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        }
    }
	public static String getRun2(String url, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
		try {
			URL urlObject = new URL(url);
			URI uri = new URI(urlObject.getProtocol(), null, urlObject.getHost(), urlObject.getPort(), urlObject.getPath(), urlObject.getQuery(), null);
			HttpGet httpGet = new HttpGet(uri);
			return getRunInternal(httpGet, socketTimeoutMillisecond, connTimeoutMillisecond);
		} catch (Throwable ex) {
			logger.error("error", ex);
			return null;
		}
	}

    public static String getRunWithHeader(String inputJson, String url,
                                          Map<String, String> header, int socketTimeoutMillisecond,
                                          int connTimeoutMillisecond) {
        try {
            HttpGet httpGet = new HttpGet();
            if (header != null) {
                for (Map.Entry e : header.entrySet()) {
                    httpGet.setHeader(ObjectUtils.toString(e.getKey()),
                            ObjectUtils.toString(e.getValue()));
                }
            }
            String reqString = url;
            if (StringUtils.isNotBlank(inputJson)) {
                reqString = reqString + "?req="
                        + URLEncoder.encode(inputJson, "utf-8");
            }

            httpGet.setURI(new URI(reqString));

            return getRunInternal(httpGet, socketTimeoutMillisecond,
                    connTimeoutMillisecond);
        } catch (Throwable ex) {
            logger.error("error", ex);
            return ex.getMessage();
        }
    }

    public static String getRunWithHeaderJson(String inputJson, String url,
                                              String headerJson, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            StringBuilder reqString = new StringBuilder(url);

            boolean flag = false;
            if (StringUtils.isNotBlank(inputJson)) {
                reqString.append("?req=").append(
                        URLEncoder.encode(inputJson, "utf-8"));
                flag = true;
            }
            if (StringUtils.isNotBlank(headerJson)) {
                if (flag) {
                    reqString.append("&");
                } else {
                    reqString.append("?");
                }
                reqString.append("header=").append(
                        URLEncoder.encode(headerJson, "utf-8"));
            }

            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(reqString.toString()));
            return getRunInternal(httpGet, socketTimeoutMillisecond,
                    connTimeoutMillisecond);
        } catch (Throwable ex) {
            logger.error("error", ex);
            return ex.getMessage();
        }
    }

    private static String postRunInternal(HttpPost httpPost, String inputJson,
                                          String url, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        long startTime = System.currentTimeMillis();
        try {
            httpPost.setURI(new URI(url));
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("req", inputJson));
            httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));

            HttpClientAgentUtils httpClient = new HttpClientAgentUtils();
            httpClient.setSocketTimeOut(socketTimeoutMillisecond);
            httpClient.setConnectionTimeOut(connTimeoutMillisecond);

            HttpClientAgentUtils.HttpResult result = httpClient.httpExecute(httpPost);
            String content = result.getContent();
            return content;
        } catch (Throwable ex) {
            logger.error("error", ex);
            return ex.getMessage();
        } finally {
//            LoggerUtil.addCheckListLog(startTime, HttpRestfulCustomTimeout.class.getName(),
//                    Thread.currentThread().getStackTrace()[1].getMethodName());
            //TODO监控发送
        }
    }

    public static String postRun(String inputJson, String url,
                                 int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            HttpPost httpPost = new HttpPost();
            return postRunInternal(httpPost, inputJson, url,
                    socketTimeoutMillisecond, connTimeoutMillisecond);
        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        }
    }
    public static String postRun(Map<String, String> param, String url,
                                 int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
            List<NameValuePair> params = new ArrayList<>();
            for (String key : param.keySet()) {
                params.add(new BasicNameValuePair(key, param.get(key)));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            HttpClientAgentUtils httpClient = new HttpClientAgentUtils();
            httpClient.setSocketTimeOut(socketTimeoutMillisecond);
            httpClient.setConnectionTimeOut(connTimeoutMillisecond);

            HttpClientAgentUtils.HttpResult result = httpClient.httpExecute(httpPost);
            String content = result.getContent();
            return content;
        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        }
    }

    public static String postRun2(String inputJson, String url, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
		long startTime = System.currentTimeMillis();
        try {
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(new URI(url));
            // List<NameValuePair> param = new ArrayList<>();
            // param.add(new BasicNameValuePair("req", inputJson));
            // httpPost.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
            httpPost.setEntity(new StringEntity(inputJson, "UTF-8"));

            HttpClientAgentUtils httpClient = new HttpClientAgentUtils();
            httpClient.setSocketTimeOut(socketTimeoutMillisecond);
            httpClient.setConnectionTimeOut(connTimeoutMillisecond);

            HttpClientAgentUtils.HttpResult result = httpClient.httpExecute(httpPost);
            String content = result.getContent();
            return content;
        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
		} finally {
            //TODO监控发送
        }
    }
    public static String postRunJson(String inputJson, String url, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        long startTime = System.currentTimeMillis();
        try {
            HttpClientAgentUtils httpClient = new HttpClientAgentUtils();
            httpClient.setSocketTimeOut(socketTimeoutMillisecond);
            httpClient.setConnectionTimeOut(connTimeoutMillisecond);
            HttpClientAgentUtils.HttpResult result = httpClient.postJson(url,inputJson);
            String content = result.getContent();
            return content;
        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        } finally {
            //TODO监控发送
        }
    }

    public static String postRunWithHeader(String inputJson, String url,
                                           Map<String, String> header, int socketTimeoutMillisecond,
                                           int connTimeoutMillisecond) {
        try {
            HttpPost httpPost = new HttpPost();
            if (header != null) {
                for (Map.Entry e : header.entrySet()) {
                    httpPost.setHeader(ObjectUtils.toString(e.getKey()),
                            ObjectUtils.toString(e.getValue()));
                }
            }

            return postRunInternal(httpPost, inputJson, url,
                    socketTimeoutMillisecond, connTimeoutMillisecond);
        } catch (Throwable ex) {
            logger.error("error", ex);
            return ex.getMessage();
        }
    }

    public static String getRunWithHeader(String url, int socketTimeoutMillisecond, int connTimeoutMillisecond) {
        try {
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(new URI(url));
            return getRunInternal(httpGet, socketTimeoutMillisecond,
                    connTimeoutMillisecond);

        } catch (Throwable ex) {
            logger.error("error", ex);
            return null;
        }
    }
}
