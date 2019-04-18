package com.sunlands.deskmate.netty;

import com.netflix.discovery.converters.Auto;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.entity.TzUser;
import com.sunlands.deskmate.enums.MessageType;
import com.sunlands.deskmate.service.LiveService;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.service.UserService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 * @author lishuai
 *
 */
@Component
@Sharable
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    
    @Autowired
    private MessageService messageService;
    @Autowired
    private LiveService liveService;
    @Autowired
    private UserService userService;

    private WebSocketServerHandshaker handshaker;
    
    private static final AttributeKey<TzUser> USER_KEY = AttributeKey.newInstance("USER_KEY");
    
    private Map<String, Set<ChannelHandlerContext>> toIdCtxsMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {//建立连接的请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {//WebSocket
            handleWebsocketFrame(ctx, (WebSocketFrame) msg, true);
        }        
    }
    
    private void notifyOnline(String status) {
        log.info("status 【{}】 notify online person ！！！", status);
        for (Map.Entry<String, Set<ChannelHandlerContext>> entry : toIdCtxsMap.entrySet()) {
            liveService.refreshOnlinePersonNum(entry.getKey()+"", entry.getValue().size());
        }
    }
    
    private void handleWebsocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame, boolean toSelf) {
        if (frame instanceof CloseWebSocketFrame) {//关闭
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
        } else if (frame instanceof PingWebSocketFrame) {//ping消息
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        } else if (frame instanceof TextWebSocketFrame) {//文本消息
            String request = ((TextWebSocketFrame) frame).text();
            TzChatRecord message = genMessageObj(ctx.channel().attr(USER_KEY).get(), request);
            String msgStr = messageService.sendMessage(message);
            Set<Integer> onlineUserIdSet = new HashSet<>();
            String destIdStr = ctx.channel().attr(USER_KEY).get().getDestIdStr();
            if (toIdCtxsMap.containsKey(destIdStr)) {
                toIdCtxsMap.get(destIdStr).add(ctx);
                toIdCtxsMap.get(destIdStr).forEach(context -> {
                    if (toSelf){
                        context.channel().write(new TextWebSocketFrame(msgStr));
                        onlineUserIdSet.add(context.channel().attr(USER_KEY).get().getId());
                    } else {
                        if (ctx.channel().attr(USER_KEY).get().getId() != context.channel().attr(USER_KEY).get().getId()){
                            context.channel().write(new TextWebSocketFrame(msgStr));
                            onlineUserIdSet.add(context.channel().attr(USER_KEY).get().getId());
                        }
                    }
                });
                // TODO 推送消息，通知
//                String destIdStr = ctx.channel().attr(USER_KEY).get().getDestIdStr();
                Integer type = ctx.channel().attr(USER_KEY).get().getType();
                if (MessageType.PRIVATE_CHAT.getType().intValue() == type.intValue()){

                } else {
                    // 请求业务获取到 此群、房间内 的成员

                }

            } else {
                log.warn("!!! can't broadcast !!!");
            }
        }
    }
    
    private TzChatRecord genMessageObj(TzUser user, String msg) {
        TzChatRecord record = new TzChatRecord();
        record.setSenderUserId(user.getId());
        record.setDestId(user.getDestIdStr());
        record.setSenderUserId(user.getId());
        record.setAvatarUrl("http://test/avataUrl");
        record.setType(user.getType());
        record.setMessage(msg);
        
        return record;
    }
    
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (HttpMethod.GET == request.method()) {
            log.info("*** request uri is: {} ***", request.uri());
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
//            String token = decoder.parameters() != null && decoder.parameters().containsKey("token") ? decoder.parameters().get("token").get(0):null;

            String typeStr = decoder.parameters() != null && decoder.parameters().containsKey("type") ? decoder.parameters().get("type").get(0):null;
            String userIdStr = decoder.parameters() != null && decoder.parameters().containsKey("userId") ? decoder.parameters().get("userId").get(0):null;
            String toIdStr = decoder.parameters() != null && decoder.parameters().containsKey("toId") ? decoder.parameters().get("toId").get(0):null;

            if (typeStr == null || userIdStr == null || toIdStr == null){
                log.info("paramter error. type = {} userId = {} toId = {}", typeStr, userIdStr, toIdStr);
                return;
            }
            Integer userId = Integer.valueOf(userIdStr);
            String destIdStr = makeDestIdStr(Integer.valueOf(typeStr), Integer.valueOf(toIdStr), Integer.valueOf(userIdStr));
            log.info(">>>>>>>destIdStr={}", destIdStr);

            // TODO 获取用户信息
            TzUser user = new TzUser();
            user.setId(userId);
            user.setDestIdStr(destIdStr);
            user.setType(1);
            user.setAvatarUrl("http://test");
            user.setName("tester" + userId);
            ctx.channel().attr(USER_KEY).set(user);
            Set<ChannelHandlerContext> ctxSet = toIdCtxsMap.get(destIdStr);
            if (ctxSet == null){
                Set<ChannelHandlerContext> cowas = new CopyOnWriteArraySet<>();
                cowas.add(ctx);
                ctxSet = toIdCtxsMap.putIfAbsent(destIdStr, cowas);
                if (ctxSet != null) {
                    ctxSet.add(ctx);
                }
            } else {
                for (ChannelHandlerContext chc : ctxSet) {
                    if (userId.intValue() == chc.channel().attr(USER_KEY).get().getId().intValue()) {
                        if (handshaker != null) {
                            log.warn("!!!! duplicate, close web socket !!!");
                            handshaker.close(chc.channel(), new CloseWebSocketFrame());
                        }
                    }
                }
                ctxSet.add(ctx);
            }
            if (request.decoderResult().isSuccess() && "websocket".equals(request.headers().get("Upgrade"))) {
                notifyOnline("init connect");
                WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("wss://" + request.headers().get("Host") + "/websocket", null, false);
                handshaker = factory.newHandshaker(request);//通过创建请求生成一个握手对象
                if (handshaker != null) {
                    handshaker.handshake(ctx.channel(), request);
                }
            } else {
                log.warn("!!! can't create handshaker, decoder result is {}  !!!", request.decoderResult().isSuccess());
            }
            WebSocketFrame msg = new TextWebSocketFrame(ctx.channel().attr(USER_KEY).get().getName() + " 上线了!");
            handleWebsocketFrame(ctx, (WebSocketFrame) msg, false);
        } else {
            log.warn("web socket request method not get");
        }
    }

    public String makeDestIdStr(Integer type, Integer toId, Integer userId) {
        String destIdStr = "";
        if (type == MessageType.PRIVATE_CHAT.getType()){
            if (userId > toId){
                destIdStr = toId + ":" + userId + ":" + type;
            } else {
                destIdStr = userId + ":" + toId + ":" + type;
            }
        } else if (type == MessageType.GROUP_CHAT.getType()){
            destIdStr = toId + ":" + type;
        } else if (type == MessageType.ROOM_CHAT.getType()){
            destIdStr = toId  + ":" + type;
        } else {
            log.warn("unknown type num type = {}", type);
        }
        return destIdStr;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        toIdCtxsMap.get(ctx.channel().attr(USER_KEY).get().getDestIdStr()).remove(ctx);
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
                ctx.channel().attr(USER_KEY).get() != null && 
                toIdCtxsMap.get(ctx.channel().attr(USER_KEY).get().getDestIdStr()) != null) {
            toIdCtxsMap.get(ctx.channel().attr(USER_KEY).get().getDestIdStr()).remove(ctx);
        }
        notifyOnline("close connect");
        super.handlerRemoved(ctx);
    }
}
