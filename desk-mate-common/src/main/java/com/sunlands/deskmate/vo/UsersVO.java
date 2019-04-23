package com.sunlands.deskmate.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "用户（道友）")
public class UsersVO {
    @ApiModelProperty(position = 10, value = "id")
    private Long id;

    @ApiModelProperty(position = 20, value = "姓名(昵称)")
    private String name;

    @ApiModelProperty(position = 30, value = "账号")
    @JsonIgnore
    private String username;

    @ApiModelProperty(position = 40, value = "手机号")
    private String phone;

    @ApiModelProperty(position = 50, value = "年龄")
    private Integer age;

    @JsonIgnore
    private String password;

    @ApiModelProperty(position = 60, value = "性别 0 男 1 女")
    private Integer sex;

    @ApiModelProperty(position = 70, value = "生日")
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date birthday;

    @ApiModelProperty(position = 80, value = "身份")
    private String identity;

    @ApiModelProperty(position = 90, value = "微信openId")
    private String mpOpenId;

    @ApiModelProperty(position = 100, value = "用户头像")
    private String avatarUrl;

    @ApiModelProperty(position = 110, value = "设备id")
    private String deviceId;

    @ApiModelProperty(position = 120, value = "学习目标id")
    private Integer currentLearnTargetId;

    @ApiModelProperty(position = 130, value = "学习目标")
    private String currentLearnTargetName;

    @ApiModelProperty(position = 140, value = "用户状态")
    private Integer status;

    @ApiModelProperty(position = 150, value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(position = 160, value = "粉丝数量")
    private Integer fansCount;

    @ApiModelProperty(position = 170, value = "关注人数")
    private Integer followCount;


    public static int getAgeByBirth(Date birthDay)  {
        int age = 0;
        Calendar cal = Calendar.getInstance();
        //出生日期晚于当前时间，无法计算
        if (Objects.isNull(birthDay) || cal.before(birthDay)) {
            return age;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        //计算整岁数
        age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }

}
