package com.sunlands.deskmate.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhaocg
 * @create 2019/4/16 10:15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupUserVO {

    private Long id;

    private String groupId;

    private String userId;

    private Integer userState;

    private Integer authState;

    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8" , pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private Byte isBuilder;

    private Byte announcement;

    private String groupNickName;

    //--------------自己添加-----------------
    private String avatarUrl;

    private String target;


}
