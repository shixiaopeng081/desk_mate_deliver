package com.sunlands.deskmate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author liuyan
 */
@Data
@ToString
public class PushMessageEntity {
    private String title;

    private String content;

    private Integer businessId;

    private Integer userId;

    private String type;

    private LocalDateTime messageDateTime;
}
