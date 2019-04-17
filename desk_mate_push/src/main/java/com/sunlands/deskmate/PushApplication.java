package com.sunlands.deskmate;

import com.sunlands.deskmate.client.TzUserCenterService;
import com.sunlands.deskmate.service.PushPayloadService;
import com.sunlands.deskmate.vo.response.BusinessResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@EnableAutoConfiguration
@SpringBootApplication
public class PushApplication {


    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(PushApplication.class, args);
        PushPayloadService bean = run.getBean(PushPayloadService.class);

        BusinessResult byIdIn = bean.tzUserCenterService.findByIdIn(Arrays.asList(1));
        System.out.println(byIdIn);

    }

}
