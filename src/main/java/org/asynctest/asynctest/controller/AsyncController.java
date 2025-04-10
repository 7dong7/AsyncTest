package org.asynctest.asynctest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asynctest.asynctest.service.AsyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncService asyncService;

    @GetMapping("/async/test1")
    public ResponseEntity<String> asyncTest1() throws ExecutionException, InterruptedException {
        log.info("/async/test1 실행중");

        System.out.println("비동기 작업 전에 처리");
        CompletableFuture<String> future = asyncService.asyncTest1();
        System.out.println("비동기 작업중에 먼저 처리");

        String message = "전처리 블로킹: " + future.get(); // 여기서 블로킹 발생: 비동기 처리를 위해 생성된 쓰레드가 처리되기를 결국 기다려되는 지점
        System.out.println("future = "+message);

        return ResponseEntity.ok("작업이 완료됨");
    }
}
