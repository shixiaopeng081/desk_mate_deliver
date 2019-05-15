package com.sunlands.deskmate.service;


import com.google.common.collect.Lists;
import com.sunlands.deskmate.client.DeskMateGroupService;
import com.sunlands.deskmate.client.DeskMateSocketService;
import com.sunlands.deskmate.domain.MessageDO;
import com.sunlands.deskmate.domain.MessageRecordDO;
import com.sunlands.deskmate.domain.MessageSystemDO;
import com.sunlands.deskmate.repository.MessageRecordRepository;
import com.sunlands.deskmate.repository.MessageRepository;
import com.sunlands.deskmate.repository.MessageSystemRepository;
import com.sunlands.deskmate.util.BeanPropertiesUtil;
import com.sunlands.deskmate.vo.GroupUserVO;
import com.sunlands.deskmate.vo.Message;
import com.sunlands.deskmate.vo.MessageDTO;
import com.sunlands.deskmate.vo.MsgChangeInformEntity;
import com.sunlands.deskmate.vo.response.BusinessResult;
import com.sunlands.deskmate.vo.response.PageResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class MessageService implements BeanPropertiesUtil {

    @Value("${message.type}")
    private String messageType;

    final  String [] userId_type_type  = {"798", "799"};//根据userId和type定义一条消息

    final  String [] userId_businessId_type  = {"1", "2", "3", "70", "71"};//根据userId和business定义一条消息

    @Transactional(rollbackFor = Exception.class)
    public void createPerson(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);

        List<MessageDO> messageDOList = new ArrayList<>();
        //小书童--即系统消息
        List<MessageSystemDO> messageSystemDOList = new ArrayList<>();
        Boolean isSystem = false;
        if(messageDTO.getType().startsWith(messageType)){
            isSystem= true;
        }

        boolean userId_businessId_type_flag = false;
        List<String> userIdBusinessIdType = Lists.newArrayList(userId_businessId_type);
        for(String str : userIdBusinessIdType){
            if(messageDO.getType().startsWith(str)){
                userId_businessId_type_flag = true;
                break;
            }
        }

        List<String> userIdTypeType = Lists.newArrayList(userId_type_type);
        boolean userId_type_type_flag = false;
        if(userIdTypeType.contains(messageDO.getType())){
            userId_type_type_flag = true;
        }

        List<Integer> userIds = messageDTO.getUserIds();
        for(Integer userId : userIds){
            MessageDO messageDODB;
            if(userId_businessId_type_flag){
                messageDODB = messageRepository.findFirstByUserIdAndBusinessId(userId, messageDO.getBusinessId());
            }else if(userId_type_type_flag){
                messageDODB = messageRepository.findFirstByUserIdAndType(userId, messageDO.getType());
            }else{
                messageDODB = messageRepository.findAllByUserIdAndBusinessIdAndType(userId, messageDO.getBusinessId(), messageDO.getType());
            }

            if(messageDODB != null){
                //修改
                messageDODB.setUnreadCount(messageDODB.getUnreadCount() + 1);
                messageDODB.setIsRead(false);
                messageDODB.setContent(messageDTO.getContent());
                messageDODB.setTitle(messageDTO.getTitle());
//            messageDODB.setAvatarUrl(messageDTO.getAvatarUrl());
                messageDOList.add(messageDODB);
            }else{
                //新增
                messageDO.setUserId(userId);
                messageDO.setUnreadCount(1);
                messageDOList.add(messageDO);
            }
            if(isSystem){
                MessageSystemDO messageSystemDO = new MessageSystemDO();
                copyNonNullProperties(messageDTO, messageSystemDO);
                messageSystemDO.setUserId(userId);
                messageSystemDOList.add(messageSystemDO);
            }
        }

        messageRepository.save(messageDOList);
        //调用消息通知接口
        noticeAndSave(messageDTO, userIds, messageSystemDOList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createGroup(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);
        //查询该群里的消息
        List<MessageDO> messageDOListDB = messageRepository.findAllByBusinessIdAndType(messageDO.getBusinessId(), messageDO.getType());
        //已发过消息的用户集合
        Map<Integer, Integer> userIdToIdMap = messageDOListDB.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> o.getId()));
        Map<Integer, MessageDO> userIdToMessageDOMap = messageDOListDB.stream().collect(Collectors.toMap(o -> o.getUserId(), o -> o));

        //根据群id查询群用户列表
        log.info("messageDTO.getBusinessId() = {} ", messageDTO.getBusinessId());
        BusinessResult<List<GroupUserVO>> groupUserByGroupId = deskMateGroupService.getGroupUserByGroupId(messageDTO.getBusinessId());
        if(groupUserByGroupId != null && !groupUserByGroupId.getData().isEmpty()){
            List<Integer> userIds = groupUserByGroupId.getData().stream().map(groupUserVO -> Integer.parseInt(groupUserVO.getUserId())).collect(Collectors.toList());
            log.info("userIds = {} ", userIds);
            userIds.removeAll(messageDTO.getExcludeUserIds());
            log.info("userIds.remove(messageDTO.getUserId()) = {} ", userIds);
            List<MessageDO> messageDOList = new ArrayList<>();

            //小书童--即系统消息
            List<MessageSystemDO> messageSystemDOList = new ArrayList<>();
            Boolean isSystem = false;
            if(messageDTO.getType().startsWith(messageType)){
                isSystem= true;
            }

            for (Integer userId : userIds){
                MessageDO messageDB = userIdToMessageDOMap.get(userId);
                if(messageDB == null){
                    //新增
                    MessageDO message = new MessageDO();
                    copyNonNullProperties(messageDO, message);
                    message.setUnreadCount(1);
                    message.setUserId(userId);
                    messageDOList.add(message);
                }else{
                    //更新
                    messageDB.setUnreadCount(messageDB.getUnreadCount() + 1);
                    messageDB.setIsRead(false);
                    messageDB.setContent(messageDTO.getContent());
                    messageDB.setTitle(messageDTO.getTitle());
                    messageDOList.add(messageDB);
                }
                if(isSystem){
                    MessageSystemDO messageSystemDO = new MessageSystemDO();
                    copyNonNullProperties(messageDTO, messageSystemDO);
                    messageSystemDO.setUserId(userId);
                    messageSystemDOList.add(messageSystemDO);
                }
            }
            log.info("messageDOList = {} ", messageDOList);
            messageRepository.save(messageDOList);
            noticeAndSave(messageDTO, userIds, messageSystemDOList);


        }
    }

    private void noticeAndSave(MessageDTO messageDTO, List<Integer> userIds, List<MessageSystemDO> messageSystemDOList) {
        CompletableFuture.runAsync(() -> {
                    //调用消息通知接口
                    MsgChangeInformEntity msgChangeInformEntity = new MsgChangeInformEntity();
                    msgChangeInformEntity.setType("999");
                    List<String> stringList = userIds.stream().map(userId -> String.valueOf(userId)).collect(Collectors.toList());
                    msgChangeInformEntity.setUserIds(stringList);
                    deskMateSocketService.inform(msgChangeInformEntity);

        });
        CompletableFuture.runAsync(() -> {
            MessageRecordDO messageRecordDO = new MessageRecordDO();
            copyNonNullProperties(messageDTO, messageRecordDO);
            log.info("messageRecordDO = {} ", messageRecordDO);
            if(messageDTO.getExcludeUserIds() != null){
                messageRecordDO.setExcludeUserIds(String.join(",", messageDTO.getExcludeUserIds().toString()));
            }
            if(messageDTO.getUserIds() != null){
                messageRecordDO.setUserIds(String.join(",", messageDTO.getUserIds().toString()));
            }
            messageRecordRepository.save(messageRecordDO);
            messageSystemRepository.save(messageSystemDOList);
        });
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

    public PageResultVO<MessageSystemDO> findSystemMessagePages(Integer userId, Integer page, Integer pageSize){
        Pageable pageable = new PageRequest(page - 1, pageSize);
        Page<MessageSystemDO> messageSystemDOS = messageSystemRepository.findAllByUserIdOrderByCreateDateTimeDesc(userId, pageable);

        PageResultVO<MessageSystemDO> pageResultVO = new PageResultVO<>();
        List<MessageSystemDO> systemDOList = new ArrayList(messageSystemDOS.getContent());
        Collections.reverse(systemDOList);
        pageResultVO.setRows(systemDOList);
        pageResultVO.setTotal(messageSystemDOS.getTotalElements());

        return pageResultVO;
    }

    private final MessageRepository messageRepository;
    private final DeskMateGroupService deskMateGroupService;
    private final MessageRecordRepository messageRecordRepository;
    private final MessageSystemRepository messageSystemRepository;
    private final DeskMateSocketService deskMateSocketService;

    public MessageService(MessageRepository messageRepository, DeskMateGroupService deskMateGroupService, MessageRecordRepository messageRecordRepository, MessageSystemRepository messageSystemRepository, DeskMateSocketService deskMateSocketService) {
        this.messageRepository = messageRepository;
        this.deskMateGroupService = deskMateGroupService;
        this.messageRecordRepository = messageRecordRepository;
        this.messageSystemRepository = messageSystemRepository;
        this.deskMateSocketService = deskMateSocketService;
    }
}
