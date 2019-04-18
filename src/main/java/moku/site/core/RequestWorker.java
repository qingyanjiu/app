package moku.site.core;

import moku.site.utils.HttpRequestUtils;

public class RequestWorker implements Runnable {

    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        String res = HttpRequestUtils.postRequestUrl(this.url);
        if(res != null){
            System.out.println(res);
        }
    }
}
