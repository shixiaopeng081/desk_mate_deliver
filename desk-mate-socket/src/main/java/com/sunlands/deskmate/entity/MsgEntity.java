package com.sunlands.deskmate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class MsgEntity {
    @ApiModelProperty(value = "发送者id")
    private String fromUserId;
    //消息的类型，如：群聊，房间聊等
    @ApiModelProperty(value = "消息类型")
    private String type;
    //类型下相关的业务Id
    @ApiModelProperty(value = "业务id 例：群id，房间id")
    private String businessId;

    //发送的消息内容，要求格式
    @ApiModelProperty(value = "内容")
    private String content;
    //消息的标题
    @ApiModelProperty(value = "标题")
    private String title;

    //发送的额外参数，要求json格式
    @ApiModelProperty(value = "扩展属性")
    private Map<String, String> extras = new HashMap<>();

}
