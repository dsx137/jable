package com.github.dsx137.jable.multithreading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IdLockerTest {

    public static void main(String[] args) {
        final IdLocker locker = new IdLocker();
        int numberOfThreads = 50; // 可以根据需要调整线程数量

        // 使用固定大小的线程池来执行测试任务
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            final String id = "resource-" + (i % 3); // 模拟多个线程访问相同资源
            executor.submit(() -> {
                // 使用 ComputeChain 进行测试
                IdLocker.ComputeChain
                        .bind(locker, id)
                        .compute(() -> {
                            System.out.println("Thread " + Thread.currentThread().getName() + " is working on " + id);
                            // 模拟一些工作
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
            });
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}