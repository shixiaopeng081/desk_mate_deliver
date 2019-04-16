package com.sunlands.deskmate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
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
    @ApiParam("标题")
    private String title;

    @ApiParam("内容")
    private String content;

    @ApiParam("头像")
    private String avatarUrl;

    @ApiParam("业务id，可能是群id，也可能是道友id")
    private Integer businessId;

    @ApiParam("接受消息的用户id")
    private Integer userId;

    @ApiParam("消息类型")
    private String type;

    @ApiParam("消息时间")
    private LocalDateTime messageDateTime;
}
