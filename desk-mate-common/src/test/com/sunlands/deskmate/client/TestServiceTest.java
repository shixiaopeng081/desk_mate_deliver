package com.sunlands.deskmate.client;

import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by yanliu on 2019/4/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestServiceTest {

    @Autowired
    private TzUserCenterService tzUserCenterService;
    @Test
    public void test1() throws Exception {

        List<Long> list = new ArrayList<>();
        list.add(1L);
        tzUserCenterService.findByIdIn(list);
    }

}