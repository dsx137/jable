package com.github.dsx137.jable.multithreading;

public class MainTest {
    public static void main(String[] args) {
        IdLocker idLocker = new IdLocker();
        idLocker.compute("hello", () -> {
            System.out.println("hello word");
        });

        IdLocker idLocker2 = new IdLocker();
        int num = IdLocker.ComputeChain
                .bind(idLocker2)
                .compute(() -> {
                    System.out.println("hello word");
                    return 1;
                });
    }
}
