package com.sunlands.deskmate.vo.response;


import com.sunlands.deskmate.vo.CommonResultMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel(value = "返回值信息集合")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResult<T> implements Serializable {
    @ApiModelProperty(position = 10, value = "状态码")
    private Long code;

    @ApiModelProperty(position = 20, value = "返回信息")
    private String message;

    @ApiModelProperty(position = 150, value = "返回结果")
    private T data;

    public static <T> BusinessResult<T> createSuccessInstance(T data) {
        return new BusinessResult<>(CommonResultMessage.SUCCESS.code, CommonResultMessage.SUCCESS.message, data);
    }

    public static <T> BusinessResult createInstance(CommonResultMessage message) {
        return new BusinessResult<>(message.getCode(), message.getMessage(), null);
    }

    public static <T> BusinessResult createInstance(CommonResultMessage message,T data) {
        return new BusinessResult<>(message.getCode(), message.getMessage(), data);
    }

    public static <T> BusinessResult createInstance(Long code,String message,T data) {
        return new BusinessResult<>(code, message, data);
    }
}
