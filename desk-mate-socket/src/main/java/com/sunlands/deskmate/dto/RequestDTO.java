package com.sunlands.deskmate.dto;

import afu.org.checkerframework.checker.igj.qual.I;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Created by yanliu on 2019/4/18.
 */
@Data
@ToString
public class RequestDTO {

    @ApiModelProperty(value = "用户id", required = true)
    private String userId;
    @ApiModelProperty(value = "目标id，例如：群id，房间id，如果是私聊，则为对方的用户id", required = true)
    private String destId;
    @ApiModelProperty(value = "已读消息最大id值", required = true)
    private String maxReadId;
    @ApiModelProperty(value = "消息类型（100：私聊 200：群聊 300：房间聊）", required = true)
    private String type;

}
