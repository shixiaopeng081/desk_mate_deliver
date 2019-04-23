package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@FeignClient(value = "tz-user-center", configuration = OauthFeignConfig.class)
public interface TzUserCenterService {

    /**
     * 查询用户列表
     *
     * @param idList
     * @return
     */
    @ApiOperation(value = "根据idList查询", notes = "根据idList查询")
    @PostMapping("/users/list")
    BusinessResult<List<UsersVO>> findByIdIn(@RequestBody List<Long> idList);
}

