package com.sunlands.deskmate.service;


import com.sunlands.deskmate.client.DeskMateGroupService;
import com.sunlands.deskmate.client.TzUserCenterService;
import com.sunlands.deskmate.domain.MessageDO;
import com.sunlands.deskmate.repository.MessageRepository;
import com.sunlands.deskmate.util.BeanPropertiesUtil;
import com.sunlands.deskmate.vo.GroupUserVO;
import com.sunlands.deskmate.vo.Message;
import com.sunlands.deskmate.vo.MessageDTO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class MessageService implements BeanPropertiesUtil {

    public void createPerson(MessageDTO messageDTO){
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

            messageRepository.save(messageDODB);
        }else{
            //新增
            messageDO.setUnreadCount(1);
            messageRepository.save(messageDO);
        }
    }

    public void createGroup(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);
        //查询该群里的消息
        List<MessageDO> messageDOListDB = messageRepository.findAllByBusinessIdAndType(messageDO.getBusinessId(), messageDO.getType());
        //已发过消息的用户集合
        Map<Integer, Integer> userIdToIdMap = messageDOListDB.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> o.getId()));
        Map<Integer, MessageDO> userIdToMessageDOMap = messageDOListDB.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> o));

        //根据群id查询群用户列表
        BusinessResult<List<GroupUserVO>> groupUserByGroupId = deskMateGroupService.getGroupUserByGroupId(messageDTO.getBusinessId());
        if(groupUserByGroupId != null && !groupUserByGroupId.getData().isEmpty()){
            List<Integer> userIds = groupUserByGroupId.getData().stream().map(groupUserVO -> Integer.parseInt(groupUserVO.getUserId())).collect(Collectors.toList());
            userIds.remove(messageDTO.getUserId());
            List<MessageDO> messageDOList = new ArrayList<>();
            for (Integer userId : userIds){
                MessageDO messageDB = userIdToMessageDOMap.get(userId);
                if(messageDB == null){
                    //新增
                    MessageDO message = new MessageDO();
                    copyNonNullProperties(messageDO, message);
                    message.setUnreadCount(1);
                    messageDOList.add(message);
                }else{
                    //更新
                    messageDB.setUnreadCount(messageDB.getUnreadCount() + 1);
                    messageDB.setIsRead(false);
                    messageDB.setContent(messageDTO.getContent());
                    messageDB.setTitle(messageDTO.getTitle());
                    messageDOList.add(messageDB);
                }
            }
            messageRepository.save(messageDOList);
        }
    }

    public List<Message> getMessageList(Integer userId){
        List<MessageDO> messageDOList = messageRepository.findAllByUserIdAndIsReadOrderByUpdateDateTimeDesc(userId, false);

        List<Message> messageList = new ArrayList<>();
        for(MessageDO messageDO : messageDOList){
            Message message = new Message();
            copyNonNullProperties(messageDO, message);
            messageList.add(message);
        }
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
    private final DeskMateGroupService deskMateGroupService;

    public MessageService(MessageRepository messageRepository, DeskMateGroupService deskMateGroupService) {
        this.messageRepository = messageRepository;
        this.deskMateGroupService = deskMateGroupService;
    }
}
