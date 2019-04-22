package com.sunlands.deskmate.enums;

/**
 * Created by yanliu on 2019/4/17.
 */
public enum MessageType {


    PRIVATE_CHAT("1", "私聊"),
    GROUP_CHAT("2", "群聊"),
    ROOM_CHAT("3", "室聊"),
    SHARE_TO_PRIVATE("4", "分享到单"),
    SHARE_TO_CONTAINER("5", "分享到群"),
    ENTER_CONTAINER("6", "进入群聊"),
    QUIT_CONTAINER("7", "退出群");

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
