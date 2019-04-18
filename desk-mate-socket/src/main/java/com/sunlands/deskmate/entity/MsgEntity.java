package com.sunlands.deskmate.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MsgEntity {
    private Integer fromUserId;
    //消息的类型，如：群聊，房间聊等
    private String type;
    //类型下相关的业务Id
    private String businessId;

    //发送的消息内容，要求格式
    private String content;
    //消息的标题
    private String title;

    //发送的额外参数，要求json格式
    private Map<String, String> extras = new HashMap<>();
}
