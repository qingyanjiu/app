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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class RequestHandler implements IRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final int DEFAULT_TASK_PROCESS_NUMBER = 10;

    private static final RequestHandler requestHandler = new RequestHandler();

    private static IContextContainer contextContainer = ContextContainer.getInstance();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

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
        Thread thread = new Thread(new RequestWorker(newTask,taskPool,allTasks,semaphore));
        executorService.execute(thread);
    }

}
