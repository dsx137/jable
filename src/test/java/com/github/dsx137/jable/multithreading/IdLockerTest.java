package com.github.dsx137.jable.multithreading;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IdLockerTest {

    private static final int numberOfThreads = 100;

    private static final int taskTime = 100;

    public static int test() {
        final IdLocker locker = new IdLocker();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        Date startDate = new Date();

        for (int i = 0; i < numberOfThreads; i++) {
            final String id = "resource-" + (i % 3);
            executor.submit(() -> {
                IdLocker.ComputeChain
                        .bind(locker, id)
                        .compute(() -> {
                            try {
                                Thread.sleep(new Random().nextInt(taskTime));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                latch.countDown();
            });
        }

        executor.shutdown();
        try {
            if (!latch.await(5, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test interrupted", e);
        }

        long totalTime = new Date().getTime() - startDate.getTime();
        return (int) totalTime;
    }

    public static void main(String[] args) {
        int totalTime = 0;
        for (int i = 0; i < 10; i++) {
            totalTime += test();
        }
        System.out.println("Average time: " + totalTime / 10 + "ms");
    }
}