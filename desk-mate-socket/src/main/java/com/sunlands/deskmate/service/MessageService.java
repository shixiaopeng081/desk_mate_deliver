package com.sunlands.deskmate.service;


import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.TzChatRecord;

import java.util.List;

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


    public List<TzChatRecord> queryUnreadRecord(RequestDTO requestDTO);

}
