package com.sunlands.deskmate.dto;

import afu.org.checkerframework.checker.igj.qual.I;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by yanliu on 2019/4/18.
 */
@Data
public class RequestDTO {

    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;
    @ApiModelProperty(value = "目标id，例如：群id，房间id，如果是私聊，则为对方的用户id", required = true)
    private Integer destId;
    @ApiModelProperty(value = "已读消息最大id值", required = true)
    private Long maxReadId;
    @ApiModelProperty(value = "消息类型（1：私聊 2：群聊 3：房间聊）", required = true)
    private Integer type;

}
