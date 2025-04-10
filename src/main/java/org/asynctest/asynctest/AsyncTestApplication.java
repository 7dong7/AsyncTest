package org.asynctest.asynctest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // 비동기 처리 활성화
public class AsyncTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncTestApplication.class, args);
    }

}
