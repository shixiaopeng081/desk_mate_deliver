package com.sunlands.deskmate.controller;

import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.TzChatRecord;
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

import static com.sunlands.deskmate.vo.response.BusinessResult.createSuccessInstance;

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

    @ApiOperation(value = "查询未读聊天信息接口")
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public BusinessResult createMessage(RequestDTO requestDTO) {
        List<TzChatRecord> tzChatRecords = messageService.queryUnreadRecord(requestDTO);
        return BusinessResult.createSuccessInstance(tzChatRecords);
    }
}
