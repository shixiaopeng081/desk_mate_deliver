package com.sunlands.deskmate.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author 
 */
@Data
@ToString
public class TzChatRecordVO implements Serializable {
    /**
     * 主键
     */
    private String id;

    /**
     * 发送者用户ID
     */
    private String fromUserId;

    /**
     * 消息类型1：普通聊天 2：分享的卡片
     */
    private String type;

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
    private Map<String, String> extras;

    /**
     * 发送到哪
     */
    private String toId;

    /**
     * 业务id，用来承载业务相关id，如群id、房间id，一般分享、邀请的时候会用到
     */
    private String contentId;

    /**
     * contentId 的 type
     */
    private String contentType;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


}