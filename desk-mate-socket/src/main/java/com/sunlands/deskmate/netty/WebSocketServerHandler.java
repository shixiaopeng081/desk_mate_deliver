package com.sunlands.deskmate.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunlands.deskmate.client.DeskMateGroupService;
import com.sunlands.deskmate.client.TzPushInformService;
import com.sunlands.deskmate.client.TzPushMessageService;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.PushInformEntity;
import com.sunlands.deskmate.entity.PushMessageEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.enums.MessageType;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.service.UserService;
import com.sunlands.deskmate.vo.response.BusinessResult;
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
import java.util.*;
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

    @Autowired
    private DeskMateGroupService deskMateGroupService;

    private static final AttributeKey<Integer> USER_KEY = AttributeKey.newInstance("USER_KEY");

    private static final ConcurrentHashMap<Integer, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Set<Integer>> onlineMap = new ConcurrentHashMap<>();

    // 用户与所在群或room对应关系
    private static final ConcurrentHashMap<Integer, Set<String>> userIdContainerMap = new ConcurrentHashMap<>();
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
            MsgEntity msgEntity =   null;
            try {
                msgEntity = JSONObject.parseObject(request, MsgEntity.class);
            } catch (Exception e){
                log.error("message format error. message = {}", request, e);
                msgEntity.setType("9000");
                msgEntity.setContent("消息格式异常");
                ctxMap.get(msgEntity.getFromUserId()).write(msgEntity);
                return;
            }
            if (MessageType.ENTER_CONTAINER.getType().equals(msgEntity.getType())){
                String key = msgEntity.getType() + ":" + msgEntity.getBusinessId();
                Set<Integer> set = new HashSet<>();
                set.add(Integer.valueOf(msgEntity.getFromUserId()));
                Set<Integer> oldSet = onlineMap.putIfAbsent(key, set);
                if (oldSet != null){
                    oldSet.add(Integer.valueOf(msgEntity.getFromUserId()));
                }
                Set<String> set2 = new HashSet<>();
                set2.add(key);
                Set<String> oldSet2 = userIdContainerMap.putIfAbsent(Integer.valueOf(msgEntity.getFromUserId()), set2);
                if (oldSet2 != null){
                    oldSet2.add(key);
                }
                return;
            }

            if (MessageType.QUIT_CONTAINER.getType().equals(msgEntity.getType())){
                String key = msgEntity.getType() + ":" + msgEntity.getBusinessId();
                onlineMap.remove(key);
                userIdContainerMap.get(Integer.valueOf(msgEntity.getFromUserId())).remove(key);
                return;
            }
            dealMsgEntiy(msgEntity);
        }
    }

    private void dealMsgEntiy(MsgEntity msgEntity) {
        log.info("deal msg start message = {}", msgEntity);
        pushMsgToContainer(msgEntity);
        log.info("deal msg end");
    }

    public void pushMsgToContainer(MsgEntity msgEntity){
        Long pId = saveChatRecord(msgEntity);
        msgEntity.setId(pId);
        List<Integer> userIdsByBusinessId = new ArrayList<>();
        if (MessageType.PRIVATE_CHAT.getType().equals(msgEntity.getType())
                || MessageType.SHARE_TO_PRIVATE.getType().equals(msgEntity.getType())){
            userIdsByBusinessId.add(Integer.valueOf(msgEntity.getFromUserId()));
            userIdsByBusinessId.add(Integer.valueOf(msgEntity.getBusinessId()));
        } else {
            userIdsByBusinessId = getUserIdsByBussinessId(Integer.valueOf(msgEntity.getBusinessId()), msgEntity.getType());
        }
        List<Integer> offlineUserIds = new ArrayList<>();
        for(Integer userId : userIdsByBusinessId){
            ChannelHandlerContext ctx = ctxMap.get(userId);
            if (ctx != null){
                ctxMap.get(userId).write(new TextWebSocketFrame(JSON.toJSONString(msgEntity)));
            } else {
                offlineUserIds.add(userId);
            }
            if (offlineUserIds.size() > 0){
                // 推送离线消息和通知
                doPushMessage(msgEntity, offlineUserIds);
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
        log.info("push inform = {} start", informEntity);
//        BusinessResult businessResult = tzPushInformService.pushInform(informEntity);
//        log.info("push inform end resut={}", businessResult);
    }

    private void doPushMessage(MsgEntity msgEntity, List<Integer> uIds) {
        PushMessageEntity messageEntity  = new PushMessageEntity();
        messageEntity.setUserIds(uIds);
        messageEntity.setType(msgEntity.getType());
        messageEntity.setBusinessId(msgEntity.getBusinessId());
        messageEntity.setContent(msgEntity.getContent());
        messageEntity.setIsGroupSend(false);
        messageEntity.setTitle(msgEntity.getTitle());
        log.info("push message = {} start", messageEntity);
//        BusinessResult businessResult = tzPushMessageService.pushMessage(messageEntity);
//        log.info("push message end result={}", businessResult);
    }

    private Long saveChatRecord(MsgEntity msgEntity) {
        TzChatRecord record = new TzChatRecord();
        record.setSenderUserId(msgEntity.getFromUserId() == null ? null : Integer.valueOf(msgEntity.getFromUserId()));
        record.setDestId(msgEntity.getBusinessId() == null ? null : Integer.valueOf(msgEntity.getBusinessId()));
        record.setType(msgEntity.getType() == null ? null : Integer.valueOf(msgEntity.getType()));
        record.setMessage(msgEntity.getContent());
        record.setTitle(msgEntity.getTitle());
        record.setExtras(JSON.toJSONString(msgEntity.getExtras()));
        messageService.saveChatRecord(record);
        return record.getId();
    }

    public Set<Integer> getOnlineUserIdByRoomId(Integer roomId, Integer type){
        String key = type + ":" + roomId;
        Set<Integer> userIdsSet = onlineMap.get(key);
        return userIdsSet;

    }

    private List<Integer> getUserIdsByBussinessId(Integer roomId, String type){
//        if (MessageType.ROOM_CHAT.getType().equals(type)){
//            // TODO
//        } else {
//            deskMateGroupService.getGroupUserByGroupId(roomId.toString());
//        }
        List<Integer> list = new ArrayList<>();
        list.add(111);
        list.add(222);
        // TODO
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
        removeFromOnlineMap(ctx);
        cause.printStackTrace();
        ctx.close();
    }

    private void removeFromOnlineMap(ChannelHandlerContext ctx) {
        Set<String> containers = userIdContainerMap.get(ctx.channel().attr(USER_KEY).get());
        if (containers != null){
            for (String key : containers){
                onlineMap.get(key).remove(ctx.channel().attr(USER_KEY).get());
            }
            userIdContainerMap.remove(ctx.channel().attr(USER_KEY).get());
        }
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
            removeFromOnlineMap(ctx);
        }
        super.handlerRemoved(ctx);
    }
}
