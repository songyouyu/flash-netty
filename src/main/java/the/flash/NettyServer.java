package the.flash;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

public class NettyServer {

    private static final int BEGIN_PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");
        serverBootstrap
                .group(boosGroup, workerGroup)
                // 服务端启动过程中的一些逻辑
                //.handler()
                // 指定IO模型
                .channel(NioServerSocketChannel.class)
                // 给服务端 channel 指定自定义属性
                .attr(AttributeKey.newInstance("serverName"), "nettyServer")
                // 给每一条连接指定自定义属性
                .childAttr(clientKey, "clientValue")
                // 给服务端 channel 设置属性. 已完成三次握手的全连接队列长度，等待 accept 调用，从全连接队列取出。
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 给每条连接设置一些TCP底层相关的属性
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 指定处理新链接数据的读写逻辑
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        System.out.println(ch.attr(clientKey).get());
                    }
                });


        bind(serverBootstrap, BEGIN_PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
