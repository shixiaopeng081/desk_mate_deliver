package com.sunlands.deskmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class PushApplication {


    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class,args);
    }

}
