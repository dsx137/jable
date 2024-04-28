package com.github.dsx137.jable.multithreading;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MainTest {

    public static void main(String[] args) {
        IdLatch<String> bus = new IdLatch<>(1, 500, TimeUnit.MILLISECONDS);

        String uuid = UUID.randomUUID().toString();

        Thread t1 = new Thread(() -> {
            try {
                sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            bus.release(uuid);
        });

        Thread t2 = new Thread(() -> {
            boolean isSuccessful = bus.await(uuid);

            if (isSuccessful) {
                System.out.println("hello");
            } else {
                System.out.println("failed");
            }
        });

        t1.start();
        t2.start();

    }

}
