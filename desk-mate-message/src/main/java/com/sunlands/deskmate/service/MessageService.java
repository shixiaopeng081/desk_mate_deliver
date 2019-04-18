package com.sunlands.deskmate.service;


import com.sunlands.deskmate.domain.MessageDO;
import com.sunlands.deskmate.repository.MessageRepository;
import com.sunlands.deskmate.util.BeanPropertiesUtil;
import com.sunlands.deskmate.vo.Message;
import com.sunlands.deskmate.vo.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class MessageService implements BeanPropertiesUtil {

    public void create(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);

        MessageDO messageDODB = messageRepository.findAllByUserIdAndBusinessIdAndType(messageDO.getUserId(), messageDO.getBusinessId(), messageDO.getType());
        if(messageDODB != null){
            //修改
            messageDODB.setUnreadCount(messageDODB.getUnreadCount() + 1);
            messageDODB.setIsRead(false);
            messageDODB.setContent(messageDTO.getContent());
            messageDODB.setTitle(messageDTO.getTitle());
//            messageDODB.setAvatarUrl(messageDTO.getAvatarUrl());
            messageDODB.setMessageDateTime(messageDTO.getMessageDateTime());

            messageRepository.save(messageDODB);
        }else{
            //新增
            messageDO.setUnreadCount(1);
            messageRepository.save(messageDO);
        }
    }

    public List<Message> getMessageList(Integer userId){
        List<MessageDO> messageDOList = messageRepository.findAllByUserIdAndIsReadOrderByMessageDateTimeDesc(userId, false);

        List<Message> messageList = new ArrayList<>();
        copyNonNullProperties(messageDOList, messageList);
        //修改为已读
        List<MessageDO> list = messageDOList.stream().map(messageDO -> {
            messageDO.setIsRead(true);
            messageDO.setUnreadCount(0);
            return messageDO;
        }).collect(Collectors.toList());
        messageRepository.save(list);
        return messageList;
    }

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
