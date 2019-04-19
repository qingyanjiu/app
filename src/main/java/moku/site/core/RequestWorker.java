//package moku.site.core;
//
//import moku.site.bean.Task;
//import moku.site.utils.HttpRequestUtils;
//
//@Deprecated
//public class RequestWorker implements Runnable {
//
//    private final Task task;
//
//    public RequestWorker(Task task){
//        this.task = task;
//    }
//
//    @Override
//    public void run() {
//        String res = "";
//        if(this.task != null)
//            res = HttpRequestUtils.postRequestUrl(this.task.getUrl());
//        if(res != null){
//            System.out.println(res);
//        }
//    }
//}
