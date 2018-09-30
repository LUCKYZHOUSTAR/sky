package lucky.sky.util.http;

import lucky.sky.util.http.client.HttpRestful;

/**
 * @author hanyk
 * @create 2018-04-17 下午6:24
 */

public class HttpTest {

    public static void main(String[] args) {
        String sd = HttpRestful.getRun2("http://www.baidu.com");
        System.out.println(sd);
    }
}
