package com.sunlands.deskmate.web;


import com.sunlands.deskmate.domain.MessageSystemDO;
import com.sunlands.deskmate.service.MessageService;
import com.sunlands.deskmate.vo.CommonResultMessage;
import com.sunlands.deskmate.vo.Message;
import com.sunlands.deskmate.vo.MessageDTO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import com.sunlands.deskmate.vo.response.PageResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "发送消息接口--多个用户/单个群")
    @PostMapping("/message")
    public BusinessResult createMessagePerson(@RequestBody MessageDTO messageDTO) {
        log.info("messageDTO = {} ", messageDTO);
        String result = checkData(messageDTO);
        if(result != null){
            BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_INVALIDE, result);
        }
        if(messageDTO.getIsGroupSend()!= null && messageDTO.getIsGroupSend()){
            messageService.createGroup(messageDTO);

        }else{
            messageService.createPerson(messageDTO);
        }
        return BusinessResult.createSuccessInstance(null);
    }

    @ApiOperation(value = "发送消息接口--适用于发送多个群")
    @PostMapping("/message/_list")
    public BusinessResult createMessagePerson(@RequestBody List<MessageDTO> messageDTOList) {
        for (MessageDTO messageDTO : messageDTOList){
            if(messageDTO.getIsGroupSend()!= null && messageDTO.getIsGroupSend()){
                messageService.createGroup(messageDTO);

            }else{
                messageService.createPerson(messageDTO);
            }
        }
        return BusinessResult.createSuccessInstance(null);
    }

    @ApiOperation(value = "根据用户id获取消息列表")
    @GetMapping("/message")
    public BusinessResult<List<Message>> getMessageList(@RequestParam Integer userId) {
        List<Message> messageList = messageService.getMessageList(userId);
        return BusinessResult.createSuccessInstance(messageList);
    }

    @ApiOperation(value = "根据用户id获取系统历史消息列表--分页")
    @GetMapping("/message/_system")
    public BusinessResult<PageResultVO<MessageSystemDO>> getMessageList(@ApiParam(value = "用户id") @RequestParam Integer userId,
                                                                        @ApiParam(value = "页码") @RequestParam(defaultValue = "1") Integer page,
                                                                        @ApiParam(value = "页码大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResultVO<MessageSystemDO> systemMessagePages = messageService.findSystemMessagePages(userId, page, pageSize);
        return BusinessResult.createSuccessInstance(systemMessagePages);
    }

    private String checkData(MessageDTO messageDTO){
        if(messageDTO.getIsGroupSend() == null || !messageDTO.getIsGroupSend()){
            List<Integer> userIds = messageDTO.getUserIds();
            if(userIds == null || userIds.size() == 0){
                return "userIdsb不能为空";
            }
        }
        return null;
    }

}
