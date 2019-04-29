package com.sunlands.deskmate.controller;

import com.sunlands.deskmate.client.DeskMateGroupService;
import com.sunlands.deskmate.client.TzLiveVideoService;
import com.sunlands.deskmate.client.TzUserCenterService;
import com.sunlands.deskmate.client.TzUserFriendService;
import com.sunlands.deskmate.mapper.TzChatRecordMapper;
import com.sunlands.deskmate.vo.*;
import com.sunlands.deskmate.dto.OnLinePeopleRequestDTO;
import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.MsgEntity;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.enums.MessageType;
import com.sunlands.deskmate.netty.WebSocketServerHandler;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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
    @Autowired
    private TzChatRecordMapper tzChatRecordMapper;
    @Autowired
    private TzLiveVideoService tzLiveVideoService;

    @ApiOperation(value = "查询未读聊天信息接口")
    @GetMapping("/unread")
//    @PreAuthorize("isAuthenticated()")
    public BusinessResult<List<TzChatRecordVO>> unreadMessage(RequestDTO requestDTO) {
        if (requestDTO.getType() == null || requestDTO.getDestId() == null || requestDTO.getUserId() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);

        }
        if (StringUtils.isBlank(requestDTO.getMaxReadId())){
            requestDTO.setMaxReadId("0");
        }
        List<TzChatRecordVO> tzChatRecords = messageService.queryUnreadRecord(requestDTO);
        return BusinessResult.createSuccessInstance(tzChatRecords);
    }


    @ApiOperation(value = "查询在线人员id接口")
    @GetMapping("/userIdsOnline")
    @PreAuthorize("isAuthenticated()")
    public BusinessResult<Set<Integer>> peopleNum(OnLinePeopleRequestDTO requestDTO) {
        if (StringUtils.isBlank(requestDTO.getDestId())){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);
        }
        Set<Integer> list = webSocketServerHandler.getOnlineUserIdByRoomId(Integer.valueOf(requestDTO.getDestId()), Integer.valueOf(MessageType.ROOM_CHAT.getType()));
        return BusinessResult.createSuccessInstance(list);
    }

    @Autowired
    private TzUserCenterService tzUserCenterService;

    @ApiOperation(value = "通过长连接发送内容接口")
    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public BusinessResult send(@RequestBody MsgEntity msgEntity) {
        if (msgEntity.getToId() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);
        }
        webSocketServerHandler.pushMsgToContainer(msgEntity);
        return BusinessResult.createSuccessInstance(null);
    }
    @Autowired
    private TzUserFriendService tzUserFriendService;
    @Autowired
    private DeskMateGroupService deskMateGroupService;

    @ApiOperation(value = "消息变动通知接口")
    @PostMapping("/inform")
    @PreAuthorize("isAnonymous()")
    public BusinessResult inform(@RequestBody MsgChangeInformEntity msgChangeInformEntity) {
        if (msgChangeInformEntity.getUserIds() == null){
            return BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_NULL);
        }
        webSocketServerHandler.inform(msgChangeInformEntity);
        return BusinessResult.createSuccessInstance(null);
    }


}
