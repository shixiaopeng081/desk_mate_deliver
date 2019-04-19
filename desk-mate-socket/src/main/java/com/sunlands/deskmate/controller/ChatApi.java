package com.sunlands.deskmate.controller;

import com.sunlands.deskmate.dto.OnLinePeopleRequestDTO;
import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.netty.WebSocketServerHandler;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.CommonResultMessage;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yanliu on 2019/4/18.
 */

@RestController
@Api(tags = "聊天内容接口")
@Slf4j
@RequestMapping("/chat")
public class ChatApi {


    @Autowired
    private MessageService messageService;
    @Autowired
    private WebSocketServerHandler webSocketServerHandler;

    @ApiOperation(value = "查询未读聊天信息接口")
    @GetMapping("/unread")
//    @PreAuthorize("isAuthenticated()")
    public BusinessResult unreadMessage(RequestDTO requestDTO) {
        if (requestDTO.getType() == null || requestDTO.getDestId() == null || requestDTO.getUserId() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);

        }
        if (requestDTO.getMaxReadId() == null){
            requestDTO.setMaxReadId(0L);
        }
        List<TzChatRecord> tzChatRecords = messageService.queryUnreadRecord(requestDTO);
        return BusinessResult.createSuccessInstance(tzChatRecords);
    }


    @ApiOperation(value = "查询在线人员id接口")
    @GetMapping("/userIdList")
//    @PreAuthorize("isAuthenticated()")
    public BusinessResult peopleNum(OnLinePeopleRequestDTO requestDTO) {
        if (requestDTO.getDestId() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);
        }
        List<Integer> list = webSocketServerHandler.getOnlineUserIdByRoomId(requestDTO.getDestId(), 1);
        return BusinessResult.createSuccessInstance(list);
    }

    @ApiOperation(value = "分享内容接口")
    @PostMapping("/share")
//    @PreAuthorize("isAuthenticated()")
    public BusinessResult share(@RequestBody MsgEntity msgEntity) {
        if (msgEntity.getBusinessId() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);
        }
        webSocketServerHandler.pushMsgToUserId(msgEntity, Integer.valueOf(msgEntity.getBusinessId()));
        return BusinessResult.createSuccessInstance(null);
    }


}
