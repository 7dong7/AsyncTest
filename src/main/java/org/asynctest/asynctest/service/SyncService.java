package org.asynctest.asynctest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncService {

    /**
     *  비동기와 동기 처리 시간에 계산
     */
    public void syncProc1() {
        log.info("동기 처리 syncProc1 처리 시작");
        try {
            Thread.sleep(3000);
            log.info("동기 처리 syncProc1 처리끝");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void syncProc2() {
        log.info("동기 처리 syncProc2 처리 시작");
        try {
            Thread.sleep(3000);
            log.info("동기 처리 syncProc2 처리끝");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void syncProc3() {
        log.info("동기 처리 syncProc3 처리 시작");
        try {
            Thread.sleep(3000);
            log.info("동기 처리 syncProc3 처리끝");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
