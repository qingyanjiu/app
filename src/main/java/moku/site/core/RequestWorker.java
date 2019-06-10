package moku.site.core;

import moku.site.bean.Task;
import moku.site.exception.RequestException;
import moku.site.utils.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RequestWorker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestWorker.class);

    private Task newTask;
    private List<Task> queuedTasks;
    private List<Task> allTasks;

    public RequestWorker(Task newTask, List<Task> queuedTasks, List<Task> allTasks){
        this.newTask = newTask;
        this.queuedTasks = queuedTasks;
        this.allTasks = allTasks;
    }

    @Override
    public void run() {
        allTasks.add(newTask);
        try {
            //新来的任务加入对象池
            queuedTasks.add(newTask);
            //任务进入处理状态
            newTask.setStatus(Task.TASK_STATUS_PROCESSING);
            //请求url
            String res = "";
            Thread.sleep(60000);
            if("POST".equals(newTask.getMethod()))
                res = HttpRequestUtils.postRequestUrl(newTask.getUrl());
            else if("GET".equals(newTask.getMethod()))
                res = HttpRequestUtils.getRequestUrl(newTask.getUrl());

            //任务完成，写入response和状态
            newTask.setResponse(res);
            newTask.setStatus(Task.TASK_STATUS_FINISHED);
            newTask.setDuringTime();
            //已完成任务信息写入日志
            logger.info("Task finished : "+newTask.getUrl());
            //已完成任务写入数据库
            //TODO
            //已完成任务从列表删除
//            allTasks.remove(newTask);
        } catch (RequestException e) {
            newTask.setDuringTime();
            newTask.setStatus(Task.TASK_STATUS_FAILED);
            newTask.setErrorMessage("request url failed:"+ newTask.getUrl());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //从对象池删除
            queuedTasks.remove(newTask);
        }
    }
}
