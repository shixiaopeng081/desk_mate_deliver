package com.sunlands.deskmate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author shixiaopeng
 */
@ApiModel("前端消息实体")
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("头像")
    private String avatarUrl;

    @ApiModelProperty("业务id")
    private Integer businessId;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("消息类型")
    private String type;

    @ApiModelProperty("未读条数")
    private Integer unreadCount;

    @ApiModelProperty("消息时间")
    private LocalDateTime messageDateTime;
}
