package moku.site.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    public static final String TASK_STATUS_WAITING = "waiting";
    public static final String TASK_STATUS_PROCESSING = "processing";
    public static final String TASK_STATUS_FINISHED = "finished";
    public static final String TASK_STATUS_FAILED = "failed";

    private String createTime;
    private String endTime;

    private String url;

    private String status = TASK_STATUS_WAITING;

    private String response;

    private String errorMessage;

    private double duringTime;

    public double getDuringTime() {
        return duringTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setDuringTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date createDate;
        Date finishDate;
        try {
            createDate = df.parse(this.createTime);
            finishDate = new Date();
            this.endTime = df.format(finishDate);
            this.duringTime = finishDate.getTime() - createDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
