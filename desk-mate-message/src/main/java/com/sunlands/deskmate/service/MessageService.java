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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class MessageService implements BeanPropertiesUtil {

    @Value("${message.type}")
    private String messageType;

    /**
     * 根据userId和type定义一条消息
     */
    private final String [] userId_type_type  = {"798", "799"};

    /**
     * 根据userId和business定义一条消息
     */
    private final  String [] userId_businessId_type;

    {
        userId_businessId_type = new String[]{"1", "2", "3", "70", "71", "74", "75"};
    }

    @Transactional( rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
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

        Boolean useridBusinessidTypeFlag = false;
        List<String> userIdBusinessIdType = Lists.newArrayList(userId_businessId_type);
        for(String str : userIdBusinessIdType){
            if(messageDO.getType().startsWith(str)){
                useridBusinessidTypeFlag = true;
                break;
            }
        }

        List<String> userIdTypeType = Lists.newArrayList(userId_type_type);
        Boolean useridTypeTypeFlag = false;
        if(userIdTypeType.contains(messageDO.getType())){
            useridTypeTypeFlag = true;
        }

        List<Integer> userIds = messageDTO.getUserIds();
        for(Integer userId : userIds){
            MessageDO messageDODB;
            if(useridBusinessidTypeFlag){
                redisFairLock(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId);
            }else if(useridTypeTypeFlag){
                messageDODB = messageRepository.findFirstByUserIdAndType(userId, messageDO.getType());
                saveOrUpdate(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId, messageDODB);
            }else{
                messageDODB = messageRepository.findAllByUserIdAndBusinessIdAndType(userId, messageDO.getBusinessId(), messageDO.getType());
                saveOrUpdate(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId, messageDODB);
            }


        }

        messageRepository.save(messageDOList);
        //调用消息通知接口
        noticeAndSave(messageDTO, userIds, messageSystemDOList);
    }

    private void redisFairLock(MessageDTO messageDTO, MessageDO messageDO, List<MessageDO> messageDOList, List<MessageSystemDO> messageSystemDOList, Boolean isSystem, Integer userId) {
        MessageDO messageDODB;
        RLock fairLock = redissonClient.getFairLock(userId + messageDO.getBusinessId());
        try {
            int waitTimeInSeconds = 3;
            int leaseTimeInSeconds = 6;
            if (fairLock.tryLock(waitTimeInSeconds, leaseTimeInSeconds, TimeUnit.SECONDS)) {
                messageDODB = messageRepository.findFirstByUserIdAndBusinessId(userId, messageDO.getBusinessId());
                saveOrUpdate(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId, messageDODB);
            } else {
                log.error("getLock fail ,userId : {}, messageDO.getBusinessId() :{}", userId, messageDO.getBusinessId());
                //无限重试
                redisFairLock(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId);
            }
        } catch (InterruptedException e) {
            log.error("", e);
        } finally {
            fairLock.unlock();
        }
    }

    private void saveOrUpdate(MessageDTO messageDTO, MessageDO messageDO, List<MessageDO> messageDOList, List<MessageSystemDO> messageSystemDOList, Boolean isSystem, Integer userId, MessageDO messageDODB) {
        if (messageDODB != null) {
            //修改
            messageDODB.setUnreadCount(messageDODB.getUnreadCount() + 1);
            messageDODB.setIsRead(false);
            messageDODB.setContent(messageDTO.getContent());
            messageDODB.setTitle(messageDTO.getTitle());
            if(!messageDODB.getType().equals(messageDO.getType())){
                messageDODB.setType(messageDO.getType());
            }
            messageDODB.setBusinessId(messageDO.getBusinessId());
            messageDOList.add(messageDODB);
        } else {
            MessageDO messageDOSave = new MessageDO();
            copyNonNullProperties(messageDO, messageDOSave);

            //新增
            messageDOSave.setUserId(userId);
            messageDOSave.setUnreadCount(1);
            messageDOList.add(messageDOSave);
        }
        if (isSystem) {
            MessageSystemDO messageSystemDO = new MessageSystemDO();
            copyNonNullProperties(messageDTO, messageSystemDO);
            messageSystemDO.setUserId(userId);
            messageSystemDOList.add(messageSystemDO);
        }
    }

    @Transactional( rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public void createGroup(MessageDTO messageDTO){
        MessageDO messageDO = new MessageDO();
        copyNonNullProperties(messageDTO, messageDO);
        //查询该群里的消息
        List<MessageDO> messageDOListDB = messageRepository.findAllByBusinessIdAndType(messageDO.getBusinessId(), messageDO.getType());
        //已发过消息的用户集合
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
                saveOrUpdate(messageDTO, messageDO, messageDOList, messageSystemDOList, isSystem, userId, messageDB);
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
        List<MessageDO> list = messageDOList.stream().peek(messageDO -> {
            messageDO.setIsRead(true);
            messageDO.setUnreadCount(0);
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
    private final RedissonClient redissonClient;

    public MessageService(MessageRepository messageRepository, DeskMateGroupService deskMateGroupService, MessageRecordRepository messageRecordRepository, MessageSystemRepository messageSystemRepository, DeskMateSocketService deskMateSocketService, RedissonClient redissonClient) {
        this.messageRepository = messageRepository;
        this.deskMateGroupService = deskMateGroupService;
        this.messageRecordRepository = messageRecordRepository;
        this.messageSystemRepository = messageSystemRepository;
        this.deskMateSocketService = deskMateSocketService;
        this.redissonClient = redissonClient;
    }
}
