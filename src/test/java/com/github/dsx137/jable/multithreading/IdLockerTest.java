package com.github.dsx137.jable.multithreading;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class IdLockerTest {

    private static final int numberOfThreads = 20;

    private static final int taskTime = 3000;

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static int test() {
        final IdLocker locker1 = new IdLocker();
        final IdLocker locker2 = new IdLocker();
        final IdLocker locker3 = new IdLocker();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads * 3);
        Date startDate = new Date();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                IdLocker.ComputeChain
                        .bind(locker1, new Random().nextInt(3))
                        .bind(locker3, new Random().nextInt(3))
                        .compute(() -> {
                            try {
                                System.out.println(counter.incrementAndGet());
                                Thread.sleep(new Random().nextInt(taskTime));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                latch.countDown();
            });
            executor.submit(() -> {
                IdLocker.ComputeChain
                        .bind(locker2, new Random().nextInt(3))
                        .bind(locker1, new Random().nextInt(3))
                        .compute(() -> {
                            try {
                                System.out.println(counter.incrementAndGet());
                                Thread.sleep(new Random().nextInt(taskTime));
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                latch.countDown();
            });
            executor.submit(() -> {
                IdLocker.ComputeChain
                        .bind(locker3, new Random().nextInt(3))
                        .bind(locker2, new Random().nextInt(3))
                        .bind(locker1, new Random().nextInt(3))
                        .compute(() -> {
                            try {
                                System.out.println(counter.incrementAndGet());
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