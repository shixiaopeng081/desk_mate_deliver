package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.constant.NameConstant;
import com.sunlands.deskmate.entity.TzUser;
import com.sunlands.deskmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lishuai
 *
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public TzUser getUserByToken(String token) {
        if (token == null) {
            return null;
        }
        return redisTemplate.hasKey(NameConstant.PREFIX_HSK_API + token) ? ((JSONObject)redisTemplate.opsForValue().get(NameConstant.PREFIX_HSK_API + token)).toJavaObject(TzUser.class) : null;
    }
}
