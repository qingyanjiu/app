package moku.site.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class HttpServer {

    private int port ;

    public HttpServer(int port){
        this.port = port;
    }

    public void start() throws InterruptedException{
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();
            System.out.println(" server start up on port : " + port);
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }

    }

}
