package com.github.sample.controller;

import com.github.sample.service.SampleService;

import java.util.concurrent.ExecutionException;

public class SampleController {


    public String testPost(String user) {
        SampleService sampleService = new SampleService();
        try {
            return sampleService.getUserName(user);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
