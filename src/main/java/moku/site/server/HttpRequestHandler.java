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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

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
        // 获取请求的uri
        String uri = req.uri();
        if(uri.indexOf(loggerUriFilter) == -1) {
            logger.info("Get request [" + uri + "]");
            Task task = new Task();
            task.setCreateTime(new Date());
            task.setUrl(uri);
            requestHandler.addTask(task);
        }
        Map result = new HashMap();
        result.put("success",true);
        String msg = JSONObject.toJSONString(result);
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 将html write到客户端
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
