package com.sunlands.deskmate.service;


import com.sunlands.deskmate.domain.MessageDO;
import com.sunlands.deskmate.repository.MessageRepository;
import com.sunlands.deskmate.util.BeanPropertiesUtil;
import com.sunlands.deskmate.vo.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class MessageService implements BeanPropertiesUtil {

    public MessageDTO create(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);

        MessageDO messageDODB = messageRepository.findAllByUserIdAndBusinessIdAndType(messageDO.getUserId(), messageDO.getBusinessId(), messageDO.getType());

        if(messageDODB != null){
            messageDODB.setUnreadCount(messageDODB.getUnreadCount() + 1);
        }else{
            messageDODB.setUnreadCount(1);
        }

        messageRepository.save(messageDO);
        copyNonNullProperties(messageDO, messageDTO);
        return messageDTO;
    }

    public MessageDTO update(MessageDTO messageDTO){

        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);
        messageRepository.save(messageDO);
        copyNonNullProperties(messageDO, messageDTO);
        return messageDTO;
    }

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
