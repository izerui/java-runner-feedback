package com.github.sample.controller;

import com.github.izerui.annotation.Tracer;
import com.github.sample.client.BaiduClient;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SampleController {

    @Autowired
    private SampleService sampleService;
    @Autowired
    private BaiduClient baiduClient;


    @Tracer("testPost")
    @GetMapping("/")
    public String testPost() throws Exception {
        for (int i = 0; i < 10; i++) {
            sampleService.writeName(UUID.randomUUID().toString(), true);
        }
//        String home = baiduClient.getHome();
//        System.out.println(home);
        return sampleService.getUserName(UUID.randomUUID().toString());
    }
}
