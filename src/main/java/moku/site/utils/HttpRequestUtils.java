package moku.site.utils;

import moku.site.exception.RequestException;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpRequestUtils {

    public static String postRequestUrl(String url) throws RequestException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String result = null;
        try {
//            Thread.sleep(60000);
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            throw new RequestException("execute post failed");
        } catch (IOException e) {
            throw new RequestException("get response content failed");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    throw new RequestException("close response failed");
                }
            }
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    throw new RequestException("close httpclient failed");
                }
            }
        }
        return result;
    }
}
