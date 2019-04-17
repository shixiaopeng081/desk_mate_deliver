package com.sunlands.deskmate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

/**
 * @author shixiaopeng
 */
@ApiModel("消息实体")
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("业务id，可能是群id，也可能是道友id")
    private Integer businessId;

    @ApiModelProperty("接受消息的用户id")
    private Integer userId;

    @ApiModelProperty("消息类型")
    private String type;

    @ApiModelProperty("消息时间")
    private LocalDateTime messageDateTime;
}
