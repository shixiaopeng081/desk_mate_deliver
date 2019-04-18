package com.sunlands.deskmate.enums;

/**
 * Created by yanliu on 2019/4/17.
 */
public enum MessageType {


    PRIVATE_CHAT(1, "私聊"),
    GROUP_CHAT(2, "群聊"),
    ROOM_CHAT(3, "室聊");

    private Integer type;
    private String name;

    MessageType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
