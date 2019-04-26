package com.sunlands.deskmate.client;

import com.sunlands.deskmate.vo.TzUserFriendInfo;
import com.sunlands.deskmate.vo.response.BusinessResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping("/user/friendsAll")
    BusinessResult<List<TzUserFriendInfo>> friends(@RequestParam(value = "userId") Long userId);
}

