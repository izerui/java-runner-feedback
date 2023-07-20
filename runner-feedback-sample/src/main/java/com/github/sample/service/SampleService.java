package com.github.sample.service;

import org.springframework.scheduling.annotation.Async;

public interface SampleService {

    /**
     * 获取用户
     * @param user
     * @return
     */
    String getUserName(String user);

    /**
     * 写入方法
     * @param user
     * @param admin
     */
    void writeName(String user, boolean admin);
}
