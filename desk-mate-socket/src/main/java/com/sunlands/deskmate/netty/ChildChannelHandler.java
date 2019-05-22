package com.sunlands.deskmate.netty;

import com.sunlands.deskmate.utils.SpringUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * @author lishuai
 *
 */
@Component
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
    
//    @Resource
//    private WebSocketServerHandler webSocketServerHandler;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        WebSocketServerHandler webSocketServerHandler = SpringUtils.getApplicationContext().getBean(WebSocketServerHandler.class);
        ch.pipeline().addLast("http-codec", new HttpServerCodec())
            .addLast("aggregator", new HttpObjectAggregator(65535))
            .addLast("http-chunked", new ChunkedWriteHandler())
            .addLast("handler", webSocketServerHandler);
    }
}
