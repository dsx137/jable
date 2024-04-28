package com.github.dsx137.jable.multithreading;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <h1>根据ID睡眠/唤醒的类</h1>
 *
 * @param <T> 睡眠/唤醒的ID类型
 */
public class IdLatch<T> {
    private final ConcurrentHashMap<T, CountDownLatch> latches = new ConcurrentHashMap<>();
    private final int downCount; // 等待次数
    private final long timeout; // 超时时间
    private final TimeUnit timeUnit; // 超时时间单位

    public IdLatch(int downCount, long timeout, TimeUnit timeUnit) {
        this.downCount = downCount;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public IdLatch() {
        this(1, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 睡眠
     *
     * @param id 睡眠ID
     * @return 是否等待成功(超时或中断返回false)
     */
    public boolean await(T id) {
        CountDownLatch latch = latches.computeIfAbsent(id, k -> new CountDownLatch(this.downCount));
        try {
            if (this.timeout == 0) {
                latch.await();
                return true;
            } else {
                return latch.await(this.timeout, this.timeUnit);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 唤醒一次
     *
     * @param id 唤醒ID
     * @return 剩余等待数
     */
    public long countDown(T id) {
        CountDownLatch latch = latches.get(id);
        if (latch != null) {
            latch.countDown();
            long count = latch.getCount();
            if (latch.getCount() == 0) {
                latches.remove(id);
            }
            return count;
        } else {
            return 0;
        }
    }
}
