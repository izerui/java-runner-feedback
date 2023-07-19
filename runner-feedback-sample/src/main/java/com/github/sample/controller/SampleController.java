package com.github.sample.controller;

import com.github.izerui.annotation.Feedback;
import com.github.sample.client.BaiduClient;
import com.github.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private SampleService sampleService;
    @Autowired
    private BaiduClient baiduClient;


    @Feedback("testPost")
    @GetMapping("/")
    public String testPost(String user) throws Exception {
        sampleService.writeName(user, true);
        String home = baiduClient.getHome();
        System.out.println(home);
        return sampleService.getUserName(user);
    }
}
