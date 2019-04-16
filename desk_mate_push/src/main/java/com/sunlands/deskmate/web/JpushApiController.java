package com.sunlands.deskmate.web;

import com.sunlands.deskmate.service.PushPayloadService;
import com.sunlands.deskmate.vo.PushDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shixiaopeng
 */
@SuppressWarnings("unused")
@Api(tags = "极光push")
@Slf4j
@RestController
public class JpushApiController {

    @Autowired
    private PushPayloadService pushPayloadService;

    @ApiOperation(value = "发起push请求接口")
    @PostMapping("/push")
    public void push(@RequestBody PushDTO pushDTO) {
        pushPayloadService.sendPushWithRegIds(pushDTO);
    }
}
