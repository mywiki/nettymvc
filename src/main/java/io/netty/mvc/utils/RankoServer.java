package io.netty.mvc.utils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;


public class RankoServer {

    private Logger logger = Logger.getLogger(getClass().getName());
    private int serverPort;
    private ServerBootstrap bootstrap;

    public RankoServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() throws InterruptedException, ServletException {
        if (this.bootstrap != null) {
            throw new IllegalStateException("Server is started, please do not repeat");
        }
        bootstrap = new ServerBootstrap();

        ApplicationContextHolder.init();

        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(
                NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpObjectAggregator(65536)); // 上传限制3M
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                pipeline.addLast("handler",new DispatcherServletHandler(ApplicationContextHolder.getDispatcherServlet()));
            }
        });
        // 配置完成，开始绑定server，通过调用sync同步方法阻塞直到绑定成功
        ChannelFuture f = bootstrap.bind(this.serverPort).sync();
        // 应用程序会一直等待，直到channel关闭
        f.channel().closeFuture().sync();
        logger.info("server listens to port " + this.serverPort);
    }
}