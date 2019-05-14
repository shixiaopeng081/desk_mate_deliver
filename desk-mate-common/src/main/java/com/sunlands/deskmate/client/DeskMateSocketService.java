package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.vo.MsgChangeInformEntity;
import com.sunlands.deskmate.vo.UsersVO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@FeignClient(value = "desk-mate-socket")
public interface DeskMateSocketService {

    @PostMapping("/chat/inform")
    BusinessResult inform(@RequestBody MsgChangeInformEntity msgChangeInformEntity);
}

