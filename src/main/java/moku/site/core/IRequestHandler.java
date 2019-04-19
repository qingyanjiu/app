package moku.site.core;

import moku.site.bean.Task;
import moku.site.exception.HandlerException;

import java.util.List;

public interface IRequestHandler {

    List<Task> getTasks();

    List<Task> getProcessingQueue();

    void doRequest(Task newTask) throws HandlerException;
}
