package moku.site.core;

import moku.site.bean.Task;
import moku.site.context.ContextContainer;
import moku.site.context.IContextContainer;
import moku.site.exception.HandlerException;
import moku.site.exception.RequestException;
import moku.site.utils.HttpRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class RequestHandler implements IRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final int DEFAULT_TASK_PROCESS_NUMBER = 10;

    private static final RequestHandler requestHandler = new RequestHandler();

    private static IContextContainer contextContainer = ContextContainer.getInstance();

    private List<Task> allTasks = new Vector();

    private RequestHandler(){
    }

    private static final List<Task> taskPool;
    private static final Semaphore semaphore;

    static {
        taskPool = new Vector();
        Map properties = contextContainer.getProperties();
        int taskProcessNumber = DEFAULT_TASK_PROCESS_NUMBER;
        if(properties.get("task.process.number") != null)
            taskProcessNumber = Integer.parseInt(properties.get("task.process.number").toString());
//        for(int i=0;i<taskProcessNumber;i++){
//            Task task = new Task();
//            taskPool.add(task);
//        }
        //定义信号量，数量与对象池一致
        semaphore = new Semaphore(taskProcessNumber);
    }

    public static RequestHandler getInstance(){
        return requestHandler;
    }


    @Override
    public List<Task> getTasks() {
        return this.allTasks;
    }

    @Override
    public List<Task> getProcessingQueue() {
        return taskPool;
    }

    @Override
    public void doRequest(Task newTask) throws HandlerException{
        allTasks.add(newTask);
        try {
            semaphore.acquire();
            //新来的任务加入对象池
            taskPool.add(newTask);
            //任务进入处理状态
            newTask.setStatus(Task.TASK_STATUS_PROCESSING);
            //请求url
            String res = HttpRequestUtils.postRequestUrl(newTask.getUrl());
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
        } catch (InterruptedException e) {
            newTask.setDuringTime();
            newTask.setStatus(Task.TASK_STATUS_FAILED);
            newTask.setErrorMessage("InterruptedException occurred");
            throw new HandlerException("task failed : "+ newTask.getUrl());
        } catch (RequestException e) {
            newTask.setDuringTime();
            newTask.setStatus(Task.TASK_STATUS_FAILED);
            newTask.setErrorMessage("request url failed:"+ newTask.getUrl());
            throw new HandlerException("task failed : "+ newTask.getUrl());
        } finally {
            //从对象池删除
            taskPool.remove(newTask);
            semaphore.release();
        }
    }

}
