package com.sunlands.deskmate.client;

import com.sunlands.deskmate.config.OauthFeignConfig;
import com.sunlands.deskmate.vo.GroupUserVO;
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
@FeignClient(value = "tz-live-video")
public interface TzLiveVideoService {

    /**
     * 查询用户id列表
     *
     * @param queryType
     * @param roomId
     * @return
     */
    @GetMapping("/liveRoom/getUserIdsByRoomId")
    BusinessResult<List<Long>> getUserIdsByRoomId(@RequestParam(value = "queryType", defaultValue = "0") Integer queryType, @RequestParam(value = "roomId") Long roomId);
}

