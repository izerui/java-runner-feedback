package com.github.sample;

import com.github.sample.controller.SampleController;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        int forNum = 100;
        CountDownLatch countDownLatch = new CountDownLatch(forNum);
        IntStream.range(0, forNum).forEach(value -> {
            new Thread(() -> {
                SampleController controller = new SampleController();
                String name = controller.testPost("uid".concat(new Random().nextInt() + ""));
                System.out.println("thread:"+Thread.currentThread().getName()+" name: " + name);
                countDownLatch.countDown();
            }).start();
        });
        countDownLatch.await();
    }
}
