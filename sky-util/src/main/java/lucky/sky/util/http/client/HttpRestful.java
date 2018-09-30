package lucky.sky.util.http.client;

import lucky.sky.util.http.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.Map;


public class HttpRestful {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpRestful.class);

	private static SystemConfiguration sc = SystemConfiguration.getInstance();

	private static int socketTimeoutMillisecond = sc.getInt("http.restful.socket.timeout.millisecond",1000);// ConfigUtil.getConfigInt("http.restful.socket.timeout.millisecond", 3000);
	private static int connTimeoutMillisecond = sc.getInt("http.restful.connection.timeout.millisecond",1000);//   ConfigUtil.getConfigInt("http.restful.connection.timeout.millisecond", 3000);

	private static String getRunReq(String reqString) throws URISyntaxException {
		return HttpRestfulCustomTimeout.getRun(null, reqString,
				socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String getRunReq(String inputJson, String url) {
		return HttpRestfulCustomTimeout.getRun(inputJson, url,
				socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String getRun2(String url) {
		return HttpRestfulCustomTimeout.getRun2(url, socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String getRunHeadReq(String inputJson, String url, String headerJson) {
		return HttpRestfulCustomTimeout.getRunWithHeaderJson(inputJson, url,
				headerJson, socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String postRunReq(String inputJson, String url) {
		return HttpRestfulCustomTimeout.postRun(inputJson, url,
				socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String getRunMapReq(String inputJson, String url,
			Map<String, String> header) {
		return HttpRestfulCustomTimeout.getRunWithHeader(inputJson, url,
				header, socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String getRunMap(Map<String, String> input, String url) {
		return HttpRestfulCustomTimeout.getRunWithProperty(input, url, socketTimeoutMillisecond, connTimeoutMillisecond);
	}
public static String postRunMap(Map<String, String> input, String url) {
		return HttpRestfulCustomTimeout.postRun(input, url, socketTimeoutMillisecond, connTimeoutMillisecond);
	}

	public static String postRunHeaderReq(String inputJson, String url,
			Map<String, String> header) {
		return HttpRestfulCustomTimeout.postRunWithHeader(inputJson, url,
				header, socketTimeoutMillisecond, connTimeoutMillisecond);
	}

}
