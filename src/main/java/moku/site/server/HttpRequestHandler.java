package moku.site.server;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import moku.site.bean.Task;
import moku.site.context.ContextContainer;
import moku.site.core.RequestHandler;
import moku.site.exception.HandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private static final String PREFIX_TASKS = "all-tasks";
    private static final String PREFIX_QUEUED_TASKS = "processing-tasks";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req){
        ContextContainer contextContainer = ContextContainer.getInstance();
        Map properties = contextContainer.getProperties();
        RequestHandler requestHandler = RequestHandler.getInstance();
        String loggerUriFilter = "";
        if(properties != null && properties.get("logger.uri.filter") != null){
            loggerUriFilter = properties.get("logger.uri.filter").toString();
        }

        //100 Continue
        if (is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.CONTINUE));
        }

        Map result = new HashMap();
        // 获取请求的uri
        String uri = req.uri();
        result.put("success",true);
        if(uri.indexOf(loggerUriFilter) == -1) {
            logger.debug("Get request [" + uri + "]");
            //查看任务列表
            if(("/"+PREFIX_TASKS).equals(uri)){
                List<Task> list = requestHandler.getTasks();
                result.put(PREFIX_TASKS,list);
            }
            else if(("/"+PREFIX_QUEUED_TASKS).equals(uri)){
                List<Task> queue = requestHandler.getProcessingQueue();
                result.put(PREFIX_QUEUED_TASKS,queue);
            }
            else {
                String url = "";
                String[] arr = uri.split("url=");
                if(arr.length > 0)
                    url = arr[1];
                Task task = new Task();
                task.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
                task.setUrl(url);
                task.setMethod(req.getMethod().toString());
                try {
                    requestHandler.doRequest(task);
                } catch (HandlerException e) {
                    result.put("success", false);
                }
            }

            String msg = JSONObject.toJSONString(result);
            // 创建http响应
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            // 设置头信息
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json; charset=UTF-8");
            //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            // 将html write到客户端
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

    }
}
