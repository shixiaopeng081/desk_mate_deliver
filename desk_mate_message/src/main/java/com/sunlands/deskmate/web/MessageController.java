package com.sunlands.deskmate.web;


import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.CommonResultMessage;
import com.sunlands.deskmate.vo.MessageDTO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

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

    @ApiOperation(value = "保存消息接口")
    @PostMapping("/message")
    public BusinessResult createMessage(@RequestBody MessageDTO messageDTO) {
        String result = checkData(messageDTO);
        if(result != null){
            BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_INVALIDE, result);
        }

        return BusinessResult.createSuccessInstance(null);
    }

    private String checkData(MessageDTO messageDTO){

        return null;
    }

}
