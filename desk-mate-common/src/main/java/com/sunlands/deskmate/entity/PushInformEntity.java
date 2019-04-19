package com.sunlands.deskmate.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author liuyan
 */
@Data
public class PushInformEntity {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("扩展属性，map格式")
    private Map<String, String> extras;

    @ApiModelProperty("用户/群/房间，不传默认个人。枚举：用户：1，群：2，房间：3")
    private Integer type;

    @ApiModelProperty("推送业务id集合,如果type是用户，就是用户id集合")
    private List<Integer> ids;
    
    @ApiModelProperty("不需要发送的用户id集合")
    private List<Integer>  excludeUserIds;


    public enum TypeEnum {
        /**
         * 用户
         */
        USER(1),

        /**
         * 群
         */
        GROUP(2),

        /**
         * 房间
         */
        ROOM(3),
        ;

        public final int code;

        TypeEnum(int code) {
            this.code = code;
        }

        public static TypeEnum byCode(int code) {
            for (TypeEnum typeEnum : TypeEnum.values()) {
                if (typeEnum.code == code) {
                    return typeEnum;
                }
            }

            throw new IllegalArgumentException("type code值不合法, input code=" + code);
        }

    }
}
