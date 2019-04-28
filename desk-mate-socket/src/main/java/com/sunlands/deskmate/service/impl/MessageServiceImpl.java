package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.entity.TzChatRecordExample;
import com.sunlands.deskmate.enums.MessageType;
import com.sunlands.deskmate.mapper.TzChatRecordMapper;
import com.sunlands.deskmate.netty.WebSocketServerHandler;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.TzChatRecordVO;
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
        log.info("【"+ message.getFromUserId() + ": " + message.getMessage()+"】");
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
    public List<TzChatRecordVO> queryUnreadRecord(RequestDTO requestDTO) {
        List<TzChatRecord> tzChatRecords = null;
        List<TzChatRecordVO> result = new ArrayList<>();
        if (MessageType.PRIVATE_CHAT.getType().equals(requestDTO.getType())){
            tzChatRecords = messageMapper.selectPrivateChatRecord(requestDTO);
        } else if (MessageType.GROUP_CHAT.getType().equals(requestDTO.getType())
                || MessageType.ROOM_CHAT.getType().equals(requestDTO.getType())){
            TzChatRecordExample example = new TzChatRecordExample();
            example.createCriteria().andToIdEqualTo(Integer.valueOf(requestDTO.getDestId())).andTypeEqualTo(Integer.valueOf(requestDTO.getType())).andIdGreaterThan(Long.valueOf(requestDTO.getMaxReadId()));
            example.setOrderByClause("create_time");
            tzChatRecords = messageMapper.selectByExample(example);
        } else {
            return new ArrayList<>();
        }
        for (TzChatRecord r : tzChatRecords){
            TzChatRecordVO vo = new TzChatRecordVO();
            vo.setId(r.getId());
            vo.setContentId(r.getContentId());
            vo.setContentType(r.getContentType());
            vo.setCreateTime(r.getCreateTime());
            vo.setToId(r.getToId());
            vo.setType(r.getType());
            vo.setExtras(JSON.parseObject(r.getExtras(), Map.class));
            vo.setFromUserId(r.getFromUserId());
            vo.setMessage(r.getMessage());
            result.add(vo);
        }
        return result;
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
