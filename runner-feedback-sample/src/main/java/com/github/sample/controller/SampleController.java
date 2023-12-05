package com.github.sample.controller;

import com.github.izerui.annotation.Tracer;
import com.github.sample.client.BaiduClient;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SampleController extends BasicController{

    @Autowired
    private SampleService sampleService;
    @Autowired
    private BaiduClient baiduClient;


    @Tracer("testPost")
    @GetMapping("/")
    public String testPost_0() throws Exception {
        this.before(null);
        for (int i = 0; i < 10; i++) {
            sampleService.writeName_0_1(UUID.randomUUID().toString(), true);
        }
        String home = baiduClient.getHome();
        System.out.println(home);
        return sampleService.getUserName_0_2(UUID.randomUUID().toString());
    }

    @Override
    protected void before(Void v) {
        System.out.println("before");
    }
}
