package com.sunlands.deskmate.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.entity.TzUser;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.service.UserService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author lishuai
 */
@Component
@Sharable
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    private WebSocketServerHandshaker handshaker;

    private static final AttributeKey<Integer> USER_KEY = AttributeKey.newInstance("USER_KEY");

    private static final ConcurrentHashMap<Integer, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {//建立连接的请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//WebSocket
            handleWebsocketFrame(ctx, (WebSocketFrame) msg, true);
        }
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, boolean toSelf) {
        if (frame instanceof CloseWebSocketFrame) {//关闭
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        } else if (frame instanceof PingWebSocketFrame) {//ping消息
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {//文本消息
            String request = ((TextWebSocketFrame) frame).text();
            MsgEntity msgEntity = JSONObject.parseObject(request, MsgEntity.class);

            dealMsgEntiy(msgEntity);
        }
    }

    public void pushMsgToUserId(Integer userId, String type, String content) {
        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setType(type);
        msgEntity.setContent(content);
        msgEntity.setFromUserId(userId);
        msgEntity.setFromUserId(ctxMap.get(userId).channel().attr(USER_KEY).get());
        ctxMap.get(userId).write(JSON.toJSONString(msgEntity));
    }




    private void dealMsgEntiy(MsgEntity msgEntity) {
        switch (msgEntity.getType()) {
            case "roomId":
                pushMsgToRoom(Integer.valueOf(msgEntity.getBusinessId()),msgEntity.getContent());
                break;
            case "system":
                break;
            default:
                ;
        }
    }

    public void pushMsgToRoom(Integer roomId,String content){
        List<Integer> userIdsByRoomId = getUserIdsByRoomId(roomId);
        for(Integer userId:userIdsByRoomId){
            pushMsgToUserId(userId,"xxxx",content);
        }

    }
    public void pushMsgToChat(Integer roomId,String content){
        List<Integer> userIdsByRoomId = getUserIdsByRoomId(roomId);
        for(Integer userId:userIdsByRoomId){
            pushMsgToUserId(userId,"xxxx2",content);
        }

    }

    public List<Integer> getOnlineUserIdByRoomId(String type,Integer roomId){
        List<Integer> userIdsByRoomId = getUserIdsByRoomId(roomId);
        List<Integer> onLineList = new ArrayList<>();
        List<Integer> offLineList = new ArrayList<>();
        for(Integer userId:userIdsByRoomId){
            if(ctxMap.get(userId)!=null) {
                onLineList.add(userId);
            }else{
                offLineList.add(userId);
            }
        }
        return onLineList;
    }

    private List<Integer> getUserIdsByRoomId(Integer roomId){
        return new ArrayList<>();
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (HttpMethod.GET == request.method()) {
            log.info("*** request uri is: {} ***", request.uri());
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
//            String token = decoder.parameters() != null && decoder.parameters().containsKey("token") ? decoder.parameters().get("token").get(0):null;

            String typeStr = decoder.parameters() != null && decoder.parameters().containsKey("type") ? decoder.parameters().get("type").get(0) : null;
            String userIdStr = decoder.parameters() != null && decoder.parameters().containsKey("userId") ? decoder.parameters().get("userId").get(0) : null;
            String toIdStr = decoder.parameters() != null && decoder.parameters().containsKey("toId") ? decoder.parameters().get("toId").get(0) : null;

            if (typeStr == null || userIdStr == null || toIdStr == null) {
                log.info("paramter error. type = {} userId = {} toId = {}", typeStr, userIdStr, toIdStr);
                return;
            }
            Integer userId = Integer.valueOf(userIdStr);
            ctx.channel().attr(USER_KEY).set(userId);

            if (request.decoderResult().isSuccess() && "websocket".equals(request.headers().get("Upgrade"))) {
                WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("wss://" + request.headers().get("Host") + "/websocket", null, false);
                handshaker = factory.newHandshaker(request);//通过创建请求生成一个握手对象
                if (handshaker != null) {
                    handshaker.handshake(ctx.channel(), request);
                }
            } else {
                log.warn("!!! can't create handshaker, decoder result is {}  !!!", request.decoderResult().isSuccess());
            }
        } else {
            log.warn("web socket request method not get");
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctxMap.remove(ctx.channel().attr(USER_KEY).get());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new java.util.Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                if (handshaker != null) {
                    //ctx.channel().write(new TextWebSocketFrame("server:主动给客户端发消息"));
                    ctx.flush();
                }
            }
        }, 1000, 1000);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (ctx != null && ctx.channel() != null && ctx.channel().attr(USER_KEY) != null &&
                ctx.channel().attr(USER_KEY).get() != null) {
            ctxMap.remove(ctx.channel().attr(USER_KEY).get());
        }
        super.handlerRemoved(ctx);
    }
}
