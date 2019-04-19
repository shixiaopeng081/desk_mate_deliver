package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.entity.TzChatRecordExample;
import com.sunlands.deskmate.enums.MessageType;
import com.sunlands.deskmate.mapper.TzChatRecordMapper;
import com.sunlands.deskmate.netty.WebSocketServerHandler;
import com.sunlands.deskmate.service.MessageService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author lishuai
 *
 */
@Service("messageService")
@Slf4j
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private TzChatRecordMapper messageMapper;
    @Autowired
    private WebSocketServerHandler webSocketServerHandler;

    @Value("${queue}")
    private String queue;
  
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public String sendMessage(TzChatRecord message) {
        log.info("【"+ message.getSenderUserId() + ": " + message.getMessage()+"】");
        String str = null;
        try {
            str = JSON.toJSONString(message);
            redisTemplate.convertAndSend(queue, str);
        } catch(Exception e) {
            log.error("method sendMessage error !" + e);
        }
        return str;
    }

    @Override
    public List<TzChatRecord> queryUnreadRecord(RequestDTO requestDTO) {
        TzChatRecordExample example = new TzChatRecordExample();
        if (MessageType.PRIVATE_CHAT.getType().equals(requestDTO.getType().toString())){
            example.createCriteria().andSenderUserIdIn(Arrays.asList(requestDTO.getUserId(), requestDTO.getDestId())).andTypeEqualTo(requestDTO.getType()).andIdGreaterThan(requestDTO.getMaxReadId());
        } else if (MessageType.GROUP_CHAT.getType().equals(requestDTO.getType().toString())
                || MessageType.ROOM_CHAT.getType().equals(requestDTO.getType().toString())){
            example.createCriteria().andDestIdEqualTo(requestDTO.getDestId()).andTypeEqualTo(requestDTO.getType()).andIdGreaterThan(requestDTO.getMaxReadId());
        }
        example.setOrderByClause("create_time");
        List<TzChatRecord> tzChatRecords = messageMapper.selectByExample(example);
        for (TzChatRecord r : tzChatRecords){
           r.setExtrasMap(JSON.parseObject(r.getExtras(), Map.class));
        }
        return tzChatRecords;
    }

    @Override
    public int saveChatRecord(TzChatRecord record) {
        return messageMapper.insertSelective(record);
    }

    public void receiveMessage(String message) {
        try {
            messageMapper.insertSelective(JSON.parseObject((String)JSON.parse(message), TzChatRecord.class));
        } catch(Exception e) {
            log.error("method receiveMessage error !" + e);
        }
    }



}
