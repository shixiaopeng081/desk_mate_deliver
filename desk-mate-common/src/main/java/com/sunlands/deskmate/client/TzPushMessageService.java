package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.entity.PushMessageEntity;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by yanliu on 2019/4/19.
 */

@FeignClient(value = "desk-mate-message", configuration = OauthFeignConfig.class)
public interface TzPushMessageService {


    /**
     * 查询用户列表
     *
     * @param pushMessageEntity
     * @return
     */
    @ApiOperation(value = "推送离线消息", notes = "推送离线消息")
    @PostMapping("/message")
    BusinessResult pushMessage(@RequestBody PushMessageEntity pushMessageEntity);
}
