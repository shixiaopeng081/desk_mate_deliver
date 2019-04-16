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
}
