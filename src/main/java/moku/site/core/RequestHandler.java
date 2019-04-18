package moku.site.core;

import moku.site.bean.Task;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RequestHandler implements IRequestHandler {

    private static final RequestHandler requestHandler = new RequestHandler();

    private List tasks = new LinkedList();

    private RequestHandler(){
    }

    public static RequestHandler getInstance(){
        return requestHandler;
    }

    @Override
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    @Override
    public void deleteTask(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public Map getWaitingQueue() {
        return null;
    }

    @Override
    public Map getProcessingQueue() {
        return null;
    }

    @Override
    public void doRequest() {
    }
}
