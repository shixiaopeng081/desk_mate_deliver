package com.sunlands.deskmate.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author liuyan
 */
@Data
public class PushMessageEntity {
    private String title;

    private String content;

    private Integer businessId;

    private Integer userId;

    private String type;

    private Date updateDateTime;
}
