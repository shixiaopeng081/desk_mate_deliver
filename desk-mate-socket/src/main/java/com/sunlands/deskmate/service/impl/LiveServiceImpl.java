package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.service.LiveService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * @author lishuai
 *
 */
@Service("liveService")
@Slf4j
public class LiveServiceImpl implements LiveService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void refreshOnlinePersonNum(String classType, int onlinePerson) {
        log.info("课程：" + classType + ", 人数：" + onlinePerson);
        redisTemplate.opsForValue().set(genOnlinePersonKey(null, classType), onlinePerson);
    }

    /**
     * 在线人数KEY
     * 
     * @param appName
     * @param streamName
     * @return
     */
    private String genOnlinePersonKey(String appName, String streamName) {
        return "";
    }
}
