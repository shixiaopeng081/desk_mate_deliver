package com.sunlands.deskmate.vo;

public enum CommonResultMessage {

    SUCCESS("0", "success","OK"),

    UNKOWN_ERROR("-1", "unkown", "系统内部错误,请稍后重试"),

    PARAMS_NOT_NULL("11000", "Required parameter cannot be an empty", "参数不能为空"),
    PARAMS_NOT_INVALIDE("11002", "Parameter is invalid", "参数值无效"),
    PASSWORD_ERROR("11003", "password error", "用户名/密码错误"),
    INVALID_USER("11004", "invalid user", "无效用户"),
    INVALID_ID("11005", "The client_id is invalid", "无效id"),
    PHONE_NOT_EXIST("11006", "phone is not exit", "手机号码未注册"),
    VERIFY_CODE_ERROR("11007", "verify code error", "验证码错误"),
    VERIFY_CODE_SEND_ERROR("11008", "verify code send error", "验证码发送错误"),
    VERIFY_CODE_SEND_FREQUENCY("11009", "verify code send frequency", "验证码发送频繁"),
    VERIFY_CODE_VERIFY_FREQUENCY("11009", "verify code verify frequency", "验证码校验频繁"),


    ;

    public final String code;
    public final String message;
    public final String data;

    CommonResultMessage(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static CommonResultMessage getResult(String code) {
        for (CommonResultMessage resultMessage : CommonResultMessage.values()) {
            if (resultMessage.code.equals(code)) {
                return resultMessage;
            }
        }
        return null;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String getData() {
        return data;
    }
}
