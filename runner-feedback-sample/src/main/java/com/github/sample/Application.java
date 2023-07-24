package com.github.sample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableJdbcRepositories
public class Application implements CommandLineRunner {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
