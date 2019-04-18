package com.sunlands.deskmate.dto;

import afu.org.checkerframework.checker.igj.qual.I;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by yanliu on 2019/4/18.
 */
@Data
public class RequestDTO {

    @ApiModelProperty("用户id")
    private Integer userId;
    @ApiModelProperty("目标id，例如：群id，房间id，如果是私聊，则为对方的用户id")
    private Integer destId;
    @ApiModelProperty("已读消息最大id值")
    private Integer maxReadNum;
    @ApiModelProperty("消息类型（1：私聊 2：群聊 3：房间聊）")
    private Integer type;
    private String destIdStr;

}
