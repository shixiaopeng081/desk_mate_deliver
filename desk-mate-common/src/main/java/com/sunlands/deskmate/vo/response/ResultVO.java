package com.sunlands.deskmate.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by huaaijia on 2015/12/10.
 */
@Data
public class ResultVO implements Serializable {
    private int state;
    private String message;
    private String alertMessage;
    private Object content;


    public ResultVO(int status, String message, String alertMessage, Object content){
        this.state = status;
        this.message = message;
        this.alertMessage = alertMessage;
        this.content = content;
    }

    public ResultVO(){
    }

}
