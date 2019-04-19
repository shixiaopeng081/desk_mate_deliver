package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.vo.GroupUserVO;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@FeignClient(value = "deskmate-group-api", configuration = OauthFeignConfig.class)
public interface DeskMateGroupService {

    @GetMapping(value = "/deskmate-group-api/group/users")
    BusinessResult<List<GroupUserVO>> getGroupUserByGroupId(@RequestParam(value = "groupId") String groupId);
}

