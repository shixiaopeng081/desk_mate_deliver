package com.sunlands.deskmate.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author 
 */
public class TzChatRecord implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 发送者用户ID
     */
    private Integer senderUserId;

    /**
     * 消息类型1：普通聊天 2：分享的卡片
     */
    private Integer type;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 时间戳
     */
    private Date createTime;

    /**
     * 额外数据
     */
    private String extras;

    private Map<String, String> extrasMap;

    /**
     * 发送到哪
     */
    private Integer destId;

    /**
     * 业务id，用来承载业务相关id，如群id、房间id，一般分享、邀请的时候会用到
     */
    private Integer contentId;

    /**
     * contentId 的 type
     */
    private Integer contentType;

    private static final long serialVersionUID = 1L;

    public Map<String, String> getExtrasMap() {
        return extrasMap;
    }

    public void setExtrasMap(Map<String, String> extrasMap) {
        this.extrasMap = extrasMap;
    }

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

    public Integer getContentId() {
        return contentId;
    }

    public void setContentId(Integer contentId) {
        this.contentId = contentId;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
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
            && (this.getExtras() == null ? other.getExtras() == null : this.getExtras().equals(other.getExtras()))
            && (this.getDestId() == null ? other.getDestId() == null : this.getDestId().equals(other.getDestId()))
            && (this.getContentId() == null ? other.getContentId() == null : this.getContentId().equals(other.getContentId()))
            && (this.getContentType() == null ? other.getContentType() == null : this.getContentType().equals(other.getContentType()));
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
        result = prime * result + ((getExtras() == null) ? 0 : getExtras().hashCode());
        result = prime * result + ((getDestId() == null) ? 0 : getDestId().hashCode());
        result = prime * result + ((getContentId() == null) ? 0 : getContentId().hashCode());
        result = prime * result + ((getContentType() == null) ? 0 : getContentType().hashCode());
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
        sb.append(", extras=").append(extras);
        sb.append(", destId=").append(destId);
        sb.append(", contentId=").append(contentId);
        sb.append(", contentType=").append(contentType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}