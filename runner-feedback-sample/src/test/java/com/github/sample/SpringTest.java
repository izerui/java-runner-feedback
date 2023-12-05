package com.github.sample;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

public class SpringTest {

    @Test
    public void test() {
        String forObject = new RestTemplate().getForObject("http://localhost:8080", String.class);
        System.out.println(forObject);
    }
}
