package com.sunlands.deskmate.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunlands.deskmate.client.TzPushInformService;
import com.sunlands.deskmate.client.TzPushMessageService;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.PushInformEntity;
import com.sunlands.deskmate.entity.PushMessageEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.enums.MessageType;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private TzPushInformService tzPushInformService;

    @Autowired
    private TzPushMessageService tzPushMessageService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {//建立连接的请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//WebSocket
            handleWebsocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
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

    private void dealMsgEntiy(MsgEntity msgEntity) {
        log.info("deal msg start message = {}", msgEntity);
        pushMsgToContainer(msgEntity);
        log.info("deal msg end");
    }

    public void pushMsgToContainer(MsgEntity msgEntity){
        List<Integer> userIdsByBusinessId = new ArrayList<>();
        if (MessageType.PRIVATE_CHAT.getType().equals(msgEntity.getType())
                || MessageType.SHARE_TO_PRIVATE.getType().equals(msgEntity.getType())){
            userIdsByBusinessId.add(Integer.valueOf(msgEntity.getFromUserId()));
            userIdsByBusinessId.add(Integer.valueOf(msgEntity.getBusinessId()));
        } else {
            userIdsByBusinessId = getUserIdsByBussinessId(Integer.valueOf(msgEntity.getBusinessId()));
        }
        List<Integer> offlineUserIds = new ArrayList<>();
        for(Integer userId : userIdsByBusinessId){
            ChannelHandlerContext ctx = ctxMap.get(userId);
            if (ctx != null){
                ctxMap.get(userId).write(new TextWebSocketFrame(JSON.toJSONString(msgEntity)));
            } else {
                offlineUserIds.add(userId);
            }
            saveChatRecord(msgEntity);
            if (offlineUserIds.size() > 0){
                // 推送离线消息
                for (Integer uId : offlineUserIds){
                    doPushMessage(msgEntity, uId);
                }
                doPushInform(msgEntity, offlineUserIds);
            }
        }
    }

    private void doPushInform(MsgEntity msgEntity, List<Integer> offlineUserIds) {
        // 推送通知
        PushInformEntity informEntity = new PushInformEntity();
        informEntity.setContent(msgEntity.getContent());
        informEntity.setIds(offlineUserIds);
        informEntity.setTitle(msgEntity.getTitle());
        informEntity.setType(1);// 枚举：用户：1，群：2，房间：3
        log.info("push inform = {} start", informEntity);
//            tzPushInformService.pushInform(informEntity);
        log.info("push inform end");
    }

    private void doPushMessage(MsgEntity msgEntity, Integer uId) {
        PushMessageEntity messageEntity  = new PushMessageEntity();
        messageEntity.setUserId(uId);
        messageEntity.setType(msgEntity.getType());
        messageEntity.setBusinessId(msgEntity.getBusinessId() == null ? null : Integer.valueOf(msgEntity.getBusinessId()));
        messageEntity.setContent(msgEntity.getContent());
        messageEntity.setMessageDateTime(LocalDateTime.now());
        messageEntity.setTitle(msgEntity.getTitle());
        log.info("push message = {} start", messageEntity);
//                tzPushMessageService.pushMessage(messageEntity);
        log.info("push message end");
    }

    private void saveChatRecord(MsgEntity msgEntity) {
        TzChatRecord record = new TzChatRecord();
        record.setSenderUserId(msgEntity.getFromUserId() == null ? null : Integer.valueOf(msgEntity.getFromUserId()));
        record.setDestId(msgEntity.getBusinessId() == null ? null : Integer.valueOf(msgEntity.getBusinessId()));
        record.setType(msgEntity.getType() == null ? null : Integer.valueOf(msgEntity.getType()));
        record.setMessage(msgEntity.getContent());
        record.setTitle(msgEntity.getTitle());
        record.setExtras(JSON.toJSONString(msgEntity.getExtras()));
        messageService.saveChatRecord(record);
    }

    public List<Integer> getOnlineUserIdByRoomId(Integer roomId, Integer type){
        List<Integer> userIdsByRoomId = getUserIdsByBussinessId(roomId);
        List<Integer> onLineList = new ArrayList<>();
        List<Integer> offLineList = new ArrayList<>();
        for(Integer userId : userIdsByRoomId){
            if(ctxMap.get(userId)!=null) {
                onLineList.add(userId);
            }else{
                offLineList.add(userId);
            }
        }
        if (type == 1){
            return onLineList;
        } else {
            return offLineList;
        }
    }

    private List<Integer> getUserIdsByBussinessId(Integer roomId){
        List<Integer> list = new ArrayList<>();
        list.add(111);
        list.add(222);
        return list;
    }


    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (HttpMethod.GET == request.method()) {
            log.info("*** request uri is: {} ***", request.uri());
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            String userIdStr = decoder.parameters() != null && decoder.parameters().containsKey("userId") ? decoder.parameters().get("userId").get(0) : null;

            if (userIdStr == null) {
                log.info("paramter error. userId = {} ", userIdStr);
                return;
            }
            ctx.channel().attr(USER_KEY).set(Integer.valueOf(userIdStr));
            ctxMap.put(Integer.valueOf(userIdStr), ctx);
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
