package com.sunlands.deskmate.domain;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author shixiaopeng
 */
@Entity(name = "tz_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "消息表")
public class MessageDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

//    private String avatarUrl;

    private Integer businessId;

    private Integer userId;

    private String type;

    @Column(insertable = false)
    private Boolean isRead;

    private Integer unreadCount;

    @Column(insertable = false, updatable = false)
    LocalDateTime createDateTime;

    @Column(insertable = false, updatable = false)
    LocalDateTime updateDateTime;
}
