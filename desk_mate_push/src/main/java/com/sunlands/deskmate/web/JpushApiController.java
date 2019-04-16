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

    @ApiOperation(value = "发起push请求接口")
    @PostMapping("/push")
    public BusinessResult push(@RequestBody PushDTO pushDTO) {
        String result = checkData(pushDTO);
        if(result != null){
            BusinessResult.createInstance(CommonResultMessage.PARAMS_NOT_INVALIDE, result);
        }
        PushResult pushResult = pushPayloadService.sendPushWithRegIds(pushDTO);
        if(pushResult.statusCode == 0){
            return BusinessResult.createSuccessInstance(null);
        }else{
            return BusinessResult.createInstance(pushResult.error.getCode() + "", pushResult.error.getMessage(), pushResult.statusCode);
        }

    }


    private String checkData(PushDTO pushDTO){
        List<String> regIds = pushDTO.getRegIds();
        if(Objects.isNull(regIds) || regIds.isEmpty()){
            return "regIds不能为空";
        }
        return null;
    }
}