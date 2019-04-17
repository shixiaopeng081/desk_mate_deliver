package com.sunlands.deskmate.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.Map;

/**
 * @author shixiaopeng
 */
@ApiModel("push实体")
@Data
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushDTO {

    @ApiParam("标题")
    private String title;

    @ApiParam("内容")
    private String content;

    @ApiParam("扩展属性，map格式")
    private Map<String, String> extras;

    @ApiParam("push对象的极光注册id集合")
    private List<String> regIds;

    @ApiParam("用户/群/房间，不传默认个人。枚举：用户：1，群：2，房间：3")
    private Integer type;

    @ApiParam("推送业务id集合,如果type是用户，就是用户id集合")
    private List<Integer> ids;

    @ApiParam("不需要发送的用户id集合")
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
