package com.sunlands.deskmate.service;


import com.sunlands.deskmate.entity.TzChatRecord;

/**
 * @author lishuai
 *
 */
public interface MessageService {
    
    /**
     * 发消息
     * 
     * @param message
     * @return
     */
    public String sendMessage(TzChatRecord message);

}
