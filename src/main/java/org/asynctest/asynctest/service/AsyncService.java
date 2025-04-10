package org.asynctest.asynctest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncService {

    @Async
    public CompletableFuture<String> asyncTest1() {
        try {
            Thread.sleep(3000);
            System.out.println("비동기 작업 스레드: " + Thread.currentThread().getName());
            return CompletableFuture.completedFuture("작업 완료");
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("에러 발생");
        }
    }
}
