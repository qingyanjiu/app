package moku.site.core;

import moku.site.bean.Task;

import java.util.Map;

public interface IRequestHandler {

    void addTask(Task task);

    void deleteTask(Task task);

    Map getWaitingQueue();

    Map getProcessingQueue();

    void doRequest();
}
