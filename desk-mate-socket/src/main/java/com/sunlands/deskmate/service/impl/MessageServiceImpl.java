package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.mapper.TzChatRecordMapper;
import com.sunlands.deskmate.service.MessageService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

/**
 * @author lishuai
 *
 */
@Service("messageService")
@Slf4j
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private TzChatRecordMapper messageMapper;

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
    
    public void receiveMessage(String message) {
        try {
            messageMapper.insertSelective(JSON.parseObject((String)JSON.parse(message), TzChatRecord.class));
        } catch(Exception e) {
            log.error("method receiveMessage error !" + e);
        }
    }
}
