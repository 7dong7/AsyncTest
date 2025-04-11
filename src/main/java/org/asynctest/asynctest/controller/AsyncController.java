package org.asynctest.asynctest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asynctest.asynctest.service.AsyncService;
import org.asynctest.asynctest.service.SyncService;
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
    private final SyncService syncService;

    /**
     *  1. 비동기 작업
     *      쓰레드는 코드를 한줄 한줄 실행 시킨다
     *      처리 도중 서비스 계층의 비동기 메소드 (@Async 가 선언된) 메소드를 만나게되면 쓰레드 풀에서 새로운 쓰레드를 가져오고
     *      기존의 쓰레드는 그 다음 코드를 이어서 실행시킴
     *
     *      새로운 쓰레드는 비동기 메소드를 실행과 동시에 바로 CompletableFuture isDone = false 인 객체를 반환하고,
     *      비동기 메소드의 처리를 진행한다
     *      
     *      비동기 메소드의 수행이 완료되면 이미 반환되어있던 CompletableFuture 비동기 메소드의 리턴값과 isDone = true 로 
     *      업데이트(변화) 한다. (반환이 아님)
     */
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

    /**
     *  2. 비동기 작업
     *      이후의 단순 반환
     *      thenApply
     */
    @GetMapping("/async/test2")
    public ResponseEntity<String> asyncTest2(){
        log.info("/async/test2 실행중");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "작업 완료";
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return "에러 발생";
            }
        }); // 비동기 작업 설정
        
        // 비동기 작업의 결과를 후속 처리
        CompletableFuture<String> result = future.thenApply(s -> "결과: " + s);

        result.thenAccept(System.out::println); // 완료 후 출력

        return ResponseEntity.ok(future.thenApply(s -> "결과: " + s).join());
    }

    /**
     *  3. 비동기 작업
     *      연속된 비동기 작업
     *      비동기 작업이후에 이어서 새로운 비동기 작업
     *      thenCompose
     */
    @GetMapping("/async/test3")
    public ResponseEntity<String> asyncTest3(){
        log.info("/async/test3 실행중");

        log.info("첫번째 비동기 처리 전에 처리");
        // 첫번째 비동기 처리
        CompletableFuture<String> first = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "첫번째 작업";
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return "에러 발생";
            }
        }); // 비동기 작업 설정
        log.info("첫번째 비동기 요청시 바로 처리");
        

        // 두 번째 비동기 처리: 첫번째 비동기 처리에 이어서 하는 비동기 처리 (연속)
        CompletableFuture<String> result = first.thenCompose(s ->
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                        return s + " 두번째 작업";
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                        return s + "에러";
                    }
                })
        );
        log.info("첫번째 비동기 요청 처리중에 처리");

        result.thenAccept(System.out::println);

        return ResponseEntity.ok(result.join());
    }

    /**
     *  4. 비동기 작업
     *      thenCompose 에 의해서 새롭게 처리되는 쓰레드는 누구인가?
     *      첫번째 비동기 처리 쓰레드: testAsync-1
     *      연속된 두번째 비동기 처리 쓰레드: testAsync-2
     *      
     *      다른 쓰레드를 새로 불러와 새로운 비동기 요청을 처리한다
     */
    @GetMapping("/async/test4")
    public ResponseEntity<String> asyncTest4(){
        log.info("/async/test4 실행중");
        log.info("컨트롤러의 쓰레드: {}", Thread.currentThread().getName());

        CompletableFuture<String> first = asyncService.asyncTest4FirstTask();
        CompletableFuture<String> second = first.thenCompose(asyncService::asyncTest4SecondTask);
        second.thenAccept(System.out::println);

        log.info("컨트롤러 다음 줄 실행");
        return ResponseEntity.ok("비동기 작업 완료");
    }


    /**
     *  5. 비동기 처리와 동기 처리의 차이
     */
    @GetMapping("/async/test5-1") // 동기처리
    public ResponseEntity<String> asyncTest5_1(){
        log.info("/async/test5-1 동기 처리중");
        long start = System.currentTimeMillis(); // 시작 시간

        syncService.syncProc1(); // 처리 1 기다림
        syncService.syncProc2(); // 처리 2 기다림
        syncService.syncProc3(); // 처리 3 기다림


        long end = System.currentTimeMillis(); // 끝 시간
        log.info("실행 처리 시간 useTime: {}", (end - start));
        /**
         *  === 동기 처리속도 기록 ===
         *      실행 처리 시간 useTime: 9022
         *      실행 처리 시간 useTime: 9028
         *      실행 처리 시간 useTime: 9014
         */
        return ResponseEntity.ok("비동기 작업 완료");
    }
    @GetMapping("/async/test5-2") // 비동기 처리
    public ResponseEntity<String> asyncTest5_2(){
        log.info("/async/test5-2 실행중");
        long start = System.currentTimeMillis(); // 시작 시간

        log.info("비동기 작업 확인 future1,2,3 전");
        CompletableFuture<Void> future1 = asyncService.asyncProc1();
        CompletableFuture<Void> future2 = asyncService.asyncProc2();
        CompletableFuture<Void> future3 = asyncService.asyncProc3();
        log.info("비동기 작업 확인 future1,2,3 후");

        log.info("비동기 작업 확인 futures 전");
        CompletableFuture<Void> futures = CompletableFuture.allOf(future1, future2, future3);
        log.info("비동기 작업 확인 futures 후");

        // 시간 측정 방식
            // 1. 블로킹
        Void join = futures.join();
        long end = System.currentTimeMillis(); // 끝 시간
        log.info("실행 처리 시간 useTime: {}", (end - start));
        
            // 2. 비동기 콜백 함수
//        futures.thenAccept(v -> {
//            long end = System.currentTimeMillis(); // 끝 시간
//            log.info("실행 처리 시간 useTime: {}", (end - start));
//        });

        /**
         *  === 비동기 처리 속도 측정 ===
         *      1. 블로킹 방식
         *          실행 처리 시간 useTime: 3007
         *          실행 처리 시간 useTime: 3004
         *          실행 처리 시간 useTime: 3002
         *
         *      2. 비동기 콜백 함수
         *          실행 처리 시간 useTime: 3009
         *          실행 처리 시간 useTime: 3015
         *          실행 처리 시간 useTime: 3003
         */
        return ResponseEntity.ok("비동기 작업 완료");
    }
    
}
