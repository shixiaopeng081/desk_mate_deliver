package com.sunlands.deskmate.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by li on 2018/5/11.
 */

@AllArgsConstructor
@Getter
public enum MsgCodeType {

    OK_CONTENT(0, "ok"),
    RISKY_CONTENT(1, "risky content"),
    CONTENT_SIZE_OUT_LIMIT(2, "too large"),
    REQ_FAIL(-1, "req fail")
    ;


    private int code;
    private String msg;

}