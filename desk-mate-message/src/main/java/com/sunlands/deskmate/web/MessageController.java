package com.sunlands.deskmate.web;


import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.CommonResultMessage;
import com.sunlands.deskmate.vo.Message;
import com.sunlands.deskmate.vo.MessageDTO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@Api(tags = "消息接口")
@Slf4j
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation(value = "发送消息接口")
    @PostMapping("/message")
    public BusinessResult createMessage(@RequestBody MessageDTO messageDTO) {
        String result = checkData(messageDTO);
        if(result != null){
            BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_INVALIDE, result);
        }
        messageService.create(messageDTO);
        return BusinessResult.createSuccessInstance(null);
    }

    @ApiOperation(value = "根据用户id获取消息列表")
    @GetMapping("/message")
    public BusinessResult getMessageList(@RequestParam Integer userId) {
        List<Message> messageList = messageService.getMessageList(userId);
        return BusinessResult.createSuccessInstance(messageList);
    }

    private String checkData(MessageDTO messageDTO){

        return null;
    }

}
