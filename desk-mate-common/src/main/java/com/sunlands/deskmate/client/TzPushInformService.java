package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.entity.PushInformEntity;
import com.sunlands.deskmate.entity.PushMessageEntity;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by yanliu on 2019/4/19.
 */

@FeignClient(value = "desk-mate-push", configuration = OauthFeignConfig.class)
public interface TzPushInformService {


    /**
     * 查询用户列表
     *
     * @param pushInformEntity
     * @return
     */
    @ApiOperation(value = "推送通知", notes = "推送通知")
    @PostMapping("/push")
    BusinessResult pushInform(@RequestBody PushInformEntity pushInformEntity);
}
