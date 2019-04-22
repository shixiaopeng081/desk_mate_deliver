package com.sunlands.deskmate.web;

import cn.jpush.api.push.PushResult;
import com.sunlands.deskmate.service.PushPayloadService;
import com.sunlands.deskmate.vo.CommonResultMessage;
import com.sunlands.deskmate.vo.PushDTO;
import com.sunlands.deskmate.vo.response.BusinessResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    @ApiOperation(value = "发起push-----请求接口")
    @PostMapping("/push")
    public BusinessResult push(@RequestBody PushDTO pushDTO) throws IOException {
        String result = checkData(pushDTO);
        if(result != null){
            BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_INVALIDE, result);
        }

        PushResult pushResult = pushPayloadService.sendPushWithRegIds(pushDTO);
        if(pushResult.statusCode == 0){
            return BusinessResult.createSuccessInstance(null);
        }else{
            return BusinessResult.createInstance((long) pushResult.statusCode, pushResult.error.getMessage(), pushResult.error);
        }
    }


    private String checkData(PushDTO pushDTO){
        List<String> ids = pushDTO.getIds();
        if(Objects.isNull(ids) || ids.isEmpty()){
            return "业务Ids不能为空";
        }
        return null;
    }
}
