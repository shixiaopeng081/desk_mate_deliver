package com.sunlands.deskmate.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by yanliu on 2019/4/18.
 */
@Data
public class OnLinePeopleRequestDTO {

    @ApiModelProperty("目标id，例如：群id，房间id，如果是私聊，则为对方的用户id")
    private Integer destId;

}
