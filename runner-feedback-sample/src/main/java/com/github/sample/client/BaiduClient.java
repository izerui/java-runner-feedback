package com.github.sample.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "baidu", url = "http://baidu.com")
public interface BaiduClient {

    @GetMapping("/")
    String getHome();
}
