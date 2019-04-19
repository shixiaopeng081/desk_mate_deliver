package com.sunlands.deskmate;

import com.sunlands.deskmate.netty.NettyWebSocketServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import javax.annotation.Resource;

@EnableAutoConfiguration
@SpringBootApplication
@EnableFeignClients
public class SocketApplication implements CommandLineRunner {

    @Value("${netty.port}")
    private Integer port;

    @Resource
    private NettyWebSocketServer nettyWebSocketServer;


    public static void main(String[] args) {
        SpringApplication.run(SocketApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyWebSocketServer.start(port);
    }

}
