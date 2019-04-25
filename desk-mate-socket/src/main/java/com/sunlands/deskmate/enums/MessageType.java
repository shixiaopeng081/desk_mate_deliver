package com.sunlands.deskmate.enums;

/**
 * Created by yanliu on 2019/4/17.
 */
public enum MessageType {


    PRIVATE_CHAT("100", "私聊"),
    GROUP_CHAT("200", "群聊"),
    ROOM_CHAT("300", "室聊"),

    ENTER_PRIVATE_CHAT("101", "进入私聊"),
    QUIT_PRIVATE_CHAT("102", "退出私聊"),

    ENTER_GROUP("201", "进入群聊"),
    QUIT_GROUP("202", "退出群聊"),
    SHARE_GROUP_TO_PRIVATE("203", "分享群到私聊"),
    SHARE_GROUP_TO_GROUP("204", "分享群到群聊"),
    SHARE_GROUP_TO_ROOM("205", "分享群到室聊"),

    ENTER_ROOM("301", "进入室聊"),
    QUIT_ROOM("302", "退出室聊"),
    SHARE_ROOM_TO_PRIVATE("303", "分享room到私聊"),
    SHARE_ROOM_TO_GROUP("304", "分享room到群聊"),
    SHARE_ROOM_TO_ROOM("305", "分享room到其它room聊");

    private String type;
    private String name;

    MessageType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
