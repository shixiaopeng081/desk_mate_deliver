package com.sunlands.deskmate.service;


import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.vo.TzChatRecordVO;

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


    public List<TzChatRecordVO> queryUnreadRecord(RequestDTO requestDTO);

    public int saveChatRecord(TzChatRecord record);

}
