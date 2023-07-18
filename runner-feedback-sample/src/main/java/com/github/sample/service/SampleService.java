package com.github.sample.service;

import com.github.sample.dao.SampleDao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SampleService {

    private ExecutorService service = Executors.newFixedThreadPool(10);

    public String getUserName(String user) throws ExecutionException, InterruptedException {
//        Future<String> future = service.submit(() -> {
//            SampleDao sampleDao = new SampleDao();
//            return sampleDao.getName(user);
//        });
//        return future.get();
        ;
        ;
        ;
        System.out.println("fff");
        SampleDao sampleDao = new SampleDao();
        sampleDao.getVoid("ff", true);
        return "sss";
    }
}
