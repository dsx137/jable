package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.random.RandomNickname;

public class MainTest {

    public static void main(String[] args) {
        RandomNickname randomNickname = new RandomNickname();
        System.out.println(randomNickname.generate(100));
    }
}
