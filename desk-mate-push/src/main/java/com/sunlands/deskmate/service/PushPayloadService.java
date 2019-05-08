package com.sunlands.deskmate.service;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sunlands.deskmate.client.DeskMateGroupService;
import com.sunlands.deskmate.client.TzLiveVideoService;
import com.sunlands.deskmate.client.TzUserCenterService;
import com.sunlands.deskmate.domain.PushRecordDO;
import com.sunlands.deskmate.repository.PushRecordRepository;
import com.sunlands.deskmate.util.BeanPropertiesUtil;
import com.sunlands.deskmate.vo.GroupUserVO;
import com.sunlands.deskmate.vo.PushDTO;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class PushPayloadService implements BeanPropertiesUtil{

    @Value("${MASTER_SECRET}")
    private String MASTER_SECRET;

    @Value("${APP_KEY}")
    private String APP_KEY;

    @Value("${APNS_PRODUCTION}")
    private boolean APNS_PRODUCTION;

    public PushResult sendPushWithRegIds(PushDTO pushDTO) throws IOException {
        PushResult result = null;
        Integer type = pushDTO.getType();
        List<Long> userIds = new ArrayList<>();

        if(type == PushDTO.TypeEnum.USER.code){
            //用户
            userIds = pushDTO.getIds().stream().map(s -> Long.parseLong(s)).collect(Collectors.toList());
        }else if(type == PushDTO.TypeEnum.GROUP.code){
            //根据群id，查询群下的所有用户
            log.info("pushDTO.getIds() = {} ", pushDTO.getIds());
            for (String groupId : pushDTO.getIds()){
                BusinessResult<List<GroupUserVO>> groupUserByGroupId = deskMateGroupService.getGroupUserByGroupId(groupId);
                userIds.addAll(groupUserByGroupId.getData().stream().map(groupUserVO -> Long.parseLong(groupUserVO.getUserId())).collect(Collectors.toList()));
            }
        }else if(type == PushDTO.TypeEnum.ROOM.code){
            //根据房间id，查询房间下的所有用户
            log.info("pushDTO.getIds() = {} ", pushDTO.getIds());
            for (String roomId : pushDTO.getIds()){
                BusinessResult<List<Long>> roomUserByRoomId = tzLiveVideoService.getUserIdsByRoomId(0, Long.parseLong(roomId));
                log.info("roomUserByRoomId.getData() = {} ", roomUserByRoomId.getData());
                if(roomUserByRoomId.getData() != null){
                    userIds.addAll(roomUserByRoomId.getData());
                }
            }
        }
        userIds.removeAll(pushDTO.getExcludeUserIds());

        if(userIds.size() == 0){
            result = new PushResult();
            result.statusCode = 0;
            return result;
        }
        //根据用户id，查询注册regid集合
        log.info("userIds = {}", userIds);
        BusinessResult businessResult = tzUserCenterService.findByIdIn(userIds);
        log.info("businessResult = {}", businessResult);
        List<UsersVO> resultData = (List<UsersVO>) businessResult.getData();
        log.info("List<UsersVO> = {}", resultData);
        List<String> regIds = resultData.stream().filter(usersVO -> usersVO.getDeviceId() != null).map(usersVO -> usersVO.getDeviceId()).collect(Collectors.toList());

        JPushClient jPushClient = new JPushClient(MASTER_SECRET, APP_KEY);
        PushPayload pushPayload = buildPushObject_android_and_ios(pushDTO, regIds);

        ObjectMapper mapper = new ObjectMapper();
        try {
            result = jPushClient.sendPush(pushPayload);
            log.info("Got result - " + result);
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            Gson gson = new Gson();
            result = gson.fromJson(e.getMessage(), PushResult.class);
            result.statusCode = e.getStatus();
        }
        PushResult finalResult = result;
        CompletableFuture.runAsync(() -> {
            try {
                PushRecordDO pushRecordDO = new PushRecordDO();


                pushRecordDO.setStatus(finalResult.statusCode);
                pushRecordDO.setTitle(pushDTO.getTitle());
                pushRecordDO.setContent(pushDTO.getContent());
                pushRecordDO.setExtras(mapper.writeValueAsString(pushDTO.getExtras()));
                pushRecordDO.setRegIds(String.join(",", regIds) );

                pushRecordRepository.saveAndFlush(pushRecordDO);
            } catch (JsonProcessingException e) {
                log.error("mapper.writeValueAsString = " + e.getMessage());
                e.printStackTrace();
            }
        });
        return result;
    }

    private PushPayload buildPushObject_android_and_ios(PushDTO pushDTO, List<String> regIds) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.registrationId(regIds))
                .setNotification(Notification.newBuilder()
                        .setAlert(pushDTO.getContent())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(pushDTO.getTitle())
                                .addExtras(pushDTO.getExtras()).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(pushDTO.getExtras()).build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(APNS_PRODUCTION)
                        .build())
                .build();
    }

    private final PushRecordRepository pushRecordRepository;
    private final TzUserCenterService tzUserCenterService;
    private final DeskMateGroupService deskMateGroupService;
    private final TzLiveVideoService tzLiveVideoService;

    public PushPayloadService(PushRecordRepository pushRecordRepository, TzUserCenterService tzUserCenterService, DeskMateGroupService deskMateGroupService, TzLiveVideoService tzLiveVideoService) {
        this.pushRecordRepository = pushRecordRepository;
        this.tzUserCenterService = tzUserCenterService;
        this.deskMateGroupService = deskMateGroupService;
        this.tzLiveVideoService = tzLiveVideoService;
    }
}
