package com.sunlands.deskmate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Created by yanliu on 2019/4/26.
 */
@Data
@ToString
public class MsgChangeInformEntity {

    @ApiModelProperty(value = "要发送给谁的userIds 的列表", required = true)
    private List<String> userIds;

    @ApiModelProperty(value = "消息变动通知类型", required = true)
    private String type;
}
