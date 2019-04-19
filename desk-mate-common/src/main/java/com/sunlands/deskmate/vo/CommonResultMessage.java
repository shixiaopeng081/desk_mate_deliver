package com.sunlands.deskmate.vo;

public enum CommonResultMessage {

    SUCCESS(0L, "success","OK"),

    UNKOWN_ERROR(-1L, "unkown", "系统内部错误,请稍后重试"),

    PARAMS_NOT_NULL(11000L, "Required parameter cannot be an empty", "参数不能为空"),
    PARAMS_NOT_INVALIDE(11002L, "Parameter is invalid", "参数值无效"),
    PASSWORD_ERROR(11003L, "password error", "用户名/密码错误"),
    INVALID_USER(11004L, "invalid user", "无效用户"),
    INVALID_ID(11005L, "The client_id is invalid", "无效id"),
    PHONE_EXIST(11006L, "phone is exit", "手机号码已注册"),
    VERIFY_CODE_ERROR(11007L, "verify code error", "验证码错误"),
    VERIFY_CODE_SEND_ERROR(11008L, "verify code send error", "验证码发送错误"),
    VERIFY_CODE_SEND_FREQUENCY(11009L, "verify code send frequency", "验证码发送频繁"),
    VERIFY_CODE_VERIFY_FREQUENCY(11010L, "verify code verify frequency", "验证码校验频繁"),


    ;

    public final Long code;
    public final String message;
    public final String data;

    CommonResultMessage(Long code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CommonResultMessage getResult(Long code) {
        for (CommonResultMessage resultMessage : CommonResultMessage.values()) {
            if (resultMessage.code.equals(code)) {
                return resultMessage;
            }
        }
        return null;
    }

    public Long getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getData() {
        return data;
    }
}
