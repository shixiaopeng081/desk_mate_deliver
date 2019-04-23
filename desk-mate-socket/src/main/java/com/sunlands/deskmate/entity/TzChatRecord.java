package com.sunlands.deskmate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author 
 */
@ApiModel(value = "聊天记录表")
public class TzChatRecord implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "消息id")
    private Long id;

    /**
     * 发送者用户ID
     */
    @ApiModelProperty(value = "发送者id")
    private Integer senderUserId;

    /**
     * 消息类型1：普通聊天 2：分享的卡片
     */
    @ApiModelProperty(value = "消息类型 1：普通聊天 2：分享的卡片")
    private Integer type;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String message;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

//    /**
//     * 头像url
//     */
//    @ApiModelProperty(value = "消息内容")
//    private String avatarUrl;

    /**
     * 分享标题
     */
    @ApiModelProperty(value = "分享标题")
    private String title;

    /**
     * 额外数据
     */
    @ApiModelProperty(value = "额外属性字符串类型")
    private String extras;

    @ApiModelProperty(value = "接收者id(可以是群id、roomId、用户id)")
    private Integer destId;

    @ApiModelProperty(value = "额外属性Map类型")
    private Map<String, String> extrasMap;

    public Map<String, String> getExtrasMap() {
        return extrasMap;
    }

    public void setExtrasMap(Map<String, String> extrasMap) {
        this.extrasMap = extrasMap;
    }

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Integer senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

//    public String getAvatarUrl() {
//        return avatarUrl;
//    }
//
//    public void setAvatarUrl(String avatarUrl) {
//        this.avatarUrl = avatarUrl;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public Integer getDestId() {
        return destId;
    }

    public void setDestId(Integer destId) {
        this.destId = destId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TzChatRecord other = (TzChatRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSenderUserId() == null ? other.getSenderUserId() == null : this.getSenderUserId().equals(other.getSenderUserId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getMessage() == null ? other.getMessage() == null : this.getMessage().equals(other.getMessage()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
//            && (this.getAvatarUrl() == null ? other.getAvatarUrl() == null : this.getAvatarUrl().equals(other.getAvatarUrl()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getExtras() == null ? other.getExtras() == null : this.getExtras().equals(other.getExtras()))
            && (this.getDestId() == null ? other.getDestId() == null : this.getDestId().equals(other.getDestId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSenderUserId() == null) ? 0 : getSenderUserId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getMessage() == null) ? 0 : getMessage().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
//        result = prime * result + ((getAvatarUrl() == null) ? 0 : getAvatarUrl().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getExtras() == null) ? 0 : getExtras().hashCode());
        result = prime * result + ((getDestId() == null) ? 0 : getDestId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", senderUserId=").append(senderUserId);
        sb.append(", type=").append(type);
        sb.append(", message=").append(message);
        sb.append(", createTime=").append(createTime);
//        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", title=").append(title);
        sb.append(", extras=").append(extras);
        sb.append(", destId=").append(destId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}