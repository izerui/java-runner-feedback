package com.github.sample.service;

import com.github.izerui.annotation.Feedback;
import com.github.sample.dao.SampleDao;

import java.util.concurrent.*;

public class SampleService {

    private ExecutorService service = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Feedback("水水水水")
    public String getUserName(String user) throws ExecutionException, InterruptedException {
        Future<String> future = service.submit(() -> {
            SampleDao sampleDao = new SampleDao();
            return sampleDao.getName(user, false);
        });
        return future.get();

//        System.out.println("fff");
//        SampleDao sampleDao = new SampleDao();
//        sampleDao.getVoid("ff", true);
//        return "sss";
    }
}
