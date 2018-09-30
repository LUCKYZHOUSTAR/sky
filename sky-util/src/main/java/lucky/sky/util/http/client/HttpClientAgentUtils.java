package lucky.sky.util.http.client;


import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class HttpClientAgentUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientAgentUtils.class);
    public static final PoolingClientConnectionManager CONNMGR;
    public static final DefaultHttpClient CLIENT;
    public static final int OK = 200;
    private static final int MAX_PER_ROUTE = 20;
    private static final int MAX_TOTAL_ROUTE = 100;
    /**
     * 自定义HTTP状态-连接超时
     */
    public static final int STATUS_CODE_TIMEOUT = 600;
    /**
     * 自定义HTT状态-其它错误
     */
    public static final int STATUS_CODE_OTHER = 603;

    /**
     * 自定义HTT状态-无状态
     */
    public static final int STATUS_CODE_NULL = 601;

    private static final String CHARSET = "UTF-8"; //默认字符编码
    /**
     * 连接超时时间,默认30秒
     */
    private int connectionTimeOut = 30000;
    /**
     * 读数据超时时间,默认200秒
     */
    private int socketTimeOut = 200000;

    private List<Header> headers;

    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        SchemeSocketFactory plain = PlainSocketFactory.getSocketFactory();
        schemeRegistry.register(new Scheme("http", 80, plain));
        schemeRegistry.register(getHttpsSupportScheme());
        CONNMGR = new PoolingClientConnectionManager(schemeRegistry);
        CONNMGR.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        CONNMGR.setMaxTotal(MAX_TOTAL_ROUTE);
        HttpParams pms = new BasicHttpParams();
        HttpProtocolParams.setVersion(pms, HttpVersion.HTTP_1_1);
        HttpProtocolParams
                .setUserAgent(
                        pms,
                        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16");
        HttpClientParams.setCookiePolicy(pms, CookiePolicy.IGNORE_COOKIES);
        CLIENT = new DefaultHttpClient(CONNMGR, pms);
    }

    /**
     * 新增参数的字符串
     * @param url
     * @param paramsStr
     * @return
     */
    public HttpResult get(String url, String paramsStr) {
        if(StringUtils.isBlank(paramsStr)){
            return httpExecute(getHttpByNullParams(url));
        }
        return httpExecute(getHttpGet(url, paramsStr));
    }

    /**
     * get方式
     * 如果没有超时时间进行限制，则采用默认的超时时间
     * @param url
     * @param params
     * @return
     */
    public HttpResult get(String url, List<NameValuePair> params) {
        if(params==null || params.size()<1){
            return httpExecute(getHttpByNullParams(url));
        }
        return httpExecute(getHttpGet(url, params));
    }

    /**
     * put方式
     * 如果没有超时时间进行限制，则采用默认的超时时间
     * @param url
     * @param params
     * @return
     */
    public HttpResult putJson(String url, String params) {
    	return httpExecute(getHttpPutJson(url, params));
    }
    
    /**
     * delete方式
     * @param url
     * @param params
     * @return
     */
    public HttpResult delete(String url, String params){
    	return httpExecute(getHttpDelete(url, params));
    }
    
    protected HttpPut getHttpPutJson(String url, String params){
    	HttpPut httpPut = new HttpPut(url);
    	httpPut.addHeader("Content-Type","application/json");
		try {
			if (StringUtils.isNotBlank(params)) {
				httpPut.setEntity(new StringEntity(params));
			}
			return httpPut;
		} catch (Exception e) {
			logger.error("getHttpPutJson UnsupportedEncodingException", e);
		}
    	return null;
    }
    
    protected HttpDelete getHttpDelete(String url, String params){
    	HttpDelete httpDelete = new HttpDelete(url);
    	return httpDelete;
    }

    protected HttpGet getHttpGet(String url, String paramsStr) {
        StringBuffer buffer = new StringBuffer(url);
        if (StringUtils.containsNone(url, "?")) {
            buffer.append("?");
        }
        buffer.append(paramsStr);

        URL urlObject = null;
        HttpGet httpGet = null;
        try {
            urlObject = new URL(buffer.toString());
            URI uri = new URI(urlObject.getProtocol(), null, urlObject.getHost(), urlObject.getPort(), urlObject.getPath(), urlObject.getQuery(), null);

            httpGet = new HttpGet(uri);
        } catch (MalformedURLException e) {
            logger.error("getHttpGet MalformedURLException", e);
            httpGet = null;
        } catch (URISyntaxException e) {
            logger.error("getHttpGet URISyntaxException", e);
            httpGet = null;
        }
        if (httpGet == null) {
            httpGet = new HttpGet(buffer.toString());
        }

        return httpGet;
    }

    protected HttpGet getHttpGet(String url, List<NameValuePair> params) {
        String paramStr = URLEncodedUtils.format(params, CHARSET);
        StringBuffer buffer = new StringBuffer(url);
        if (StringUtils.containsNone(url, "?")) {
            buffer.append("?");
        }
        buffer.append(paramStr);
        return new HttpGet(buffer.toString());
    }

    protected HttpGet getHttpByNullParams(String url) {
        StringBuffer buffer = new StringBuffer(url);
        return new HttpGet(buffer.toString());
    }

    /**
     * post方式
     * 如果没有超时时间进行限制，则采用默认的超时时间
     * @param url
     * @param params
     * @return
     */
    public HttpResult post(String url, List<NameValuePair> params) {
        return httpExecute(getHttpPost(url, params));
    }

    public HttpResult post(String url, String content) {
        return httpExecute(getHttpPost(url, content));
    }

    public HttpResult postJson(String url, String content) {
        return httpExecute(getJsonHttpPost(url, content));
    }

    protected HttpPost getHttpPost(String url, List<NameValuePair> params) {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new UrlEncodedFormEntity(params, CHARSET));
            return post;
        } catch (Exception e) {
            logger.error("getHttpPost UnsupportedEncodingException",e);
        }
        return null;
    }

    protected HttpPost getHttpPost(String url, String content) {
        HttpPost post = new HttpPost(url);
        try {
            post.setEntity(new StringEntity(content, CHARSET));
            return post;
        } catch (Exception e) {
            logger.error("getHttpPost UnsupportedEncodingException",e);
        }
        return null;
    }

    protected HttpPost getJsonHttpPost(String url, String content){
        HttpPost post = new HttpPost(url);

        try {
            StringEntity stringEntity = new StringEntity(content, CHARSET);
            stringEntity.setContentType("application/json");
            post.setEntity(stringEntity);
            return post;
        } catch (Exception e) {
            logger.error("getHttpPost UnsupportedEncodingException",e);
        }
        return null;
    }


    private void setHeader(HttpUriRequest request) {
        if (headers != null) {
            for (Header header : headers) {
                if (header != null) {
                    request.addHeader(header);
                }
            }
        }
    }

    public HttpResult httpExecute(HttpUriRequest request) {
        HttpEntity entity = null;
        HttpResult result = new HttpResult();
        try {
            setHeader(request);
            setTimeOut(request);
            HttpResponse response = CLIENT.execute(request);
            StatusLine status = response.getStatusLine();
            entity = response.getEntity();
            result.setHeaders(response.getAllHeaders());
            result.setStatusCode(status != null?status.getStatusCode():STATUS_CODE_NULL);
            if (status != null && status.getStatusCode() == OK) {
                result.setContent(EntityUtils.toString(entity,"utf-8"));
            } else {
                result.setContent("");
            }
        } catch (SocketTimeoutException e) {
            result.setStatusCode(STATUS_CODE_TIMEOUT);
            logger.error("HttpExecute SocketTimeoutException, url:" + request.getURI().toString() + " HttpCode:"+result.getStatusCode(), e);
        } catch (ConnectTimeoutException e) {
            result.setStatusCode(STATUS_CODE_TIMEOUT);
            logger.error("HttpExecute ConnectTimeoutException, url:" + request.getURI().toString(), e);
        } catch (Exception e) {
            result.setStatusCode(STATUS_CODE_OTHER);
            logger.error("HttpExecute exception, url:" + request.getURI().toString(), e);
        } finally {
            if (request != null) {
                request.abort();
            }
        }
        return result;
    }

    private void setTimeOut(HttpUriRequest request) {
        request.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeOut);
        request.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeOut);
    }

    public List<Header> getHeaders() {
        if (headers == null) {
            headers = new ArrayList<Header>();
        }
        return headers;
    }

    public int getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    private static Scheme getHttpsSupportScheme() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return new Scheme("https", 443, ssf);
        } catch (NoSuchAlgorithmException e) {
            logger.error("getHttpsSupportScheme NoSuchAlgorithmException.",e);
        } catch (KeyManagementException e) {
            logger.error("getHttpsSupportScheme KeyManagementException.",e);
        }
        return null;
    }

    public class HttpResult {
        private Header[] headers;
        private String content;
        private int statusCode = 200;

        public Header[] getHeaders() {
            return headers;
        }

        public void setHeaders(Header[] headers) {
            this.headers = headers;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }
        
    }
	
}
