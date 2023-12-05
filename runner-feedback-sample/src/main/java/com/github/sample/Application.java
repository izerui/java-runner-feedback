package com.github.sample;

import com.github.sample.aspectj.TenantMethodAspect;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableJdbcRepositories
public class Application implements CommandLineRunner {

    @Bean
    public TenantMethodAspect tenantMethodAspect() {
        return new TenantMethodAspect();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
