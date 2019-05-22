package com.sunlands.deskmate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class MsgEntity {

    @ApiModelProperty(value = "消息id")
    private  String id;
    @ApiModelProperty(value = "消息类型(参考wiki:长链接数据格式及 type 定义)", required = true)
    private String type;
    @ApiModelProperty(value = "发送到哪里，一般为 个人id、群id、或房间id，根据 type 字段区分（比如 type 为 204，那么 toId 就是 群id）", required = true)
    private String toId;
    @ApiModelProperty(value = "发送者id", required = true)
    private String fromUserId;
    @ApiModelProperty(value = "消息创建时间")
    private String createTime;
    @ApiModelProperty(value = "消息内容", required = true)
    private String message;
    @ApiModelProperty(value = "业务id，用来承载业务相关id，如群id、房间id，一般分享、邀请的时候会用到")
    private  String contentId;
    @ApiModelProperty(value = "contentId 的 type")
    private  String contentType;
    @ApiModelProperty(value = "扩展属性")
    private Map<String, String> extras = new HashMap<>();
    private String retype;

}
