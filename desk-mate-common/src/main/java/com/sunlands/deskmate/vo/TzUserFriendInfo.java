package com.sunlands.deskmate.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author zhangxiaobin
 * @title: TzUserFriendInfo
 * @description: TODO
 * @date 2019/4/1511:47
 */
@Data
@Entity
@JsonSerialize
@DynamicUpdate
@DynamicInsert
@ApiModel(value = "道友相关信息")
public class TzUserFriendInfo {

    @Id
    @GeneratedValue
    private Long id;
    private Long userId; //主人
    private Long friendsUserId; //主人的道友
    private Integer friendsType; //关系类型 1恋人 2朋友 3同学
    private String friendsUserNickName; //道友的昵称
    private String friendsUserAvatar; //道友的头像
    private String friendsUserDesc;
    private Date createTime;

}
