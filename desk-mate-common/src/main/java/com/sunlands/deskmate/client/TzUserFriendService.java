package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.vo.TzUserFriendInfo;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@FeignClient(value = "tz-user-friend")
public interface TzUserFriendService {

    /**
     * 查询道友列表
     * @param userId
     * @return
     */
    @GetMapping("/user/friends")
    BusinessResult<List<TzUserFriendInfo>> friends(@RequestParam(value = "userId") Long userId);
}

