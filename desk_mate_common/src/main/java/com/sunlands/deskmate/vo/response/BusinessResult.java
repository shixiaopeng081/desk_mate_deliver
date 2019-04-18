package com.sunlands.deskmate.vo.response;


import com.sunlands.deskmate.vo.CommonResultMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResult implements Serializable {

    private String code;

    private String message;

    private Object data;

    public static BusinessResult createSuccessInstance(Object data) {
        return new BusinessResult(CommonResultMessage.SUCCESS.code, CommonResultMessage.SUCCESS.message, data);
    }

    public static BusinessResult createInstance(CommonResultMessage message) {
        return new BusinessResult(message.getCode(), message.getMessage(), message.getData());
    }

    public static BusinessResult createInstance(CommonResultMessage message,Object data) {
        return new BusinessResult(message.getCode(), message.getMessage(), data);
    }

    public static BusinessResult createInstance(String code,String message,Object data) {
        return new BusinessResult(code, message, data);
    }
}
