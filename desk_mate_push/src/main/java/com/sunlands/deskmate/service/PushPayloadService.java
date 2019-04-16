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
import com.sunlands.deskmate.vo.PushDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shixiaopeng
 */
@Slf4j
@Service
public class PushPayloadService {

    @Value("${MASTER_SECRET}")
    private String MASTER_SECRET;

    @Value("${APP_KEY}")
    private String APP_KEY;

    @Value("${APNS_PRODUCTION}")
    private boolean APNS_PRODUCTION;

    public PushResult sendPushWithRegIds(PushDTO pushDTO) {
        JPushClient jPushClient = new JPushClient(MASTER_SECRET, APP_KEY);
        PushPayload pushPayload = buildPushObject_android_and_ios(pushDTO);
        PushResult result = null;
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
        }
        return result;
    }

    private PushPayload buildPushObject_android_and_ios(PushDTO pushDTO) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.registrationId(pushDTO.getRegIds()))
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
}
