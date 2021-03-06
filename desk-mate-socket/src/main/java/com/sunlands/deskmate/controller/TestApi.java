package com.sunlands.deskmate.controller;

import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.mapper.TzChatRecordMapper;
import com.sunlands.deskmate.sstwds.ContentSecCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by yanliu on 2019/5/7.
 */

@RestController
@Slf4j
@RequestMapping("/test")
public class TestApi {

    @Autowired
    private TzChatRecordMapper tzChatRecordMapper;

    @Autowired
    private ContentSecCheck contentSecCheck;

    @RequestMapping("/test")
    public String test111(){
        String result = contentSecCheck.filterSensitiveWords("这是聊天内容测试傻吊，傻逼，上访去公安局七濑莉娜好的");

        System.out.println("result="+result);
        return "success";
    }



    @RequestMapping("/apiOne")
    public String test(){
        RequestDTO dto = new RequestDTO();
        dto.setUserId("111");
        dto.setDestId("999");
        dto.setType("200");
        dto.setMaxReadId("0");
        List<TzChatRecord> tzChatRecords = tzChatRecordMapper.selectGroupChatRecord(dto);
        List<TzChatRecord> tzChatRecords1 = tzChatRecordMapper.selectRoomChatRecord(dto);
        System.out.println(tzChatRecords);
        return "success";
    }
}
