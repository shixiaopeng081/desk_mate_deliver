package com.sunlands.deskmate.service;


import com.sunlands.deskmate.entity.TzUser;

/**
 * @author lishuai
 *
 */
public interface UserService {

    /**
     * 获取用户信息
     * 
     * @param token
     * @return
     */
    public TzUser getUserByToken(String token);
}
