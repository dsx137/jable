package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.base.Wrapper;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * <h1>使用ReentrantLock和委托实现的泛型原子类</h1>
 *
 * @param <T> 值的类型
 */
public class Atomic<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private Wrapper<T> value;

    public <R> R compute(Function<Wrapper<T>, R> action) {
        this.lock.lock();
        try {
            return action.apply(this.value);
        } finally {
            this.lock.unlock();
        }
    }
}
