package com.sunlands.deskmate.service.impl;

import com.sunlands.deskmate.CommonApplication;
import com.sunlands.deskmate.SocketApplication;
import com.sunlands.deskmate.client.TzUserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by yanliu on 2019/4/18.
 */

@SpringBootTest(classes = SocketApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class MessageServiceImplTest {

    @Autowired
    private TzUserCenterService tzUserCenterService;


    @Test
    public void test(){
        System.out.println("test");

//        List<Long> list = new ArrayList<>();
//        list.add(1L);
//        tzUserCenterService.findByIdIn(list);




    }


}