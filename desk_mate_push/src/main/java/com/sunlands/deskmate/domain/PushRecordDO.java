package com.sunlands.deskmate.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shixiaopeng
 */
@Entity(name = "tz_push_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "push推送记录表")
public class PushRecordDO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    private String extras;

    private String regIds;

    private Integer status;

    @Column(insertable = false, updatable = false)
    LocalDateTime createDateTime;

    @Transient
    public List<String> getregIdList() {
        if (Objects.nonNull(regIds)) {
            List<String> roleList = Arrays.stream(this.regIds.split(","))
                    .filter(StringUtils::hasLength)
                    .map(String::trim)
                    .collect(Collectors.toList());
            return Collections.unmodifiableList(roleList);
        } else {
            return Collections.emptyList();
        }
    }

}
