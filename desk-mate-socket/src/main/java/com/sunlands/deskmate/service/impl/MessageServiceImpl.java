package com.sunlands.deskmate.service.impl;

import com.netflix.discovery.converters.Auto;
import com.sunlands.deskmate.dto.RequestDTO;
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

import java.util.List;

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
        String destIdStr = webSocketServerHandler.makeDestIdStr(requestDTO.getType(), requestDTO.getDestId(), requestDTO.getUserId());
        TzChatRecordExample example = new TzChatRecordExample();
        example.createCriteria().andDestIdEqualTo(destIdStr).andIdGreaterThan(requestDTO.getMaxReadId());
        example.setOrderByClause("create_time");
        return messageMapper.selectByExample(example);
    }

    public void receiveMessage(String message) {
        try {
            messageMapper.insertSelective(JSON.parseObject((String)JSON.parse(message), TzChatRecord.class));
        } catch(Exception e) {
            log.error("method receiveMessage error !" + e);
        }
    }
}
