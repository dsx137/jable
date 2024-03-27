package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.base.Wrapper;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h1>使用ReentrantLock和委托实现的泛型原子类</h1>
 *
 * @param <T> 值的类型
 */
public class Locker<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private final Wrapper<T> value;

    public Locker(T value) {
        this.value = Wrapper.of(value);
    }

    public <R> R compute(Function<Wrapper<T>, R> action) {
        this.lock.lock();
        R res;
        try {
            res = action.apply(this.value);
        } finally {
            this.lock.unlock();
        }
        return res;
    }

    public void compute(Consumer<Wrapper<T>> action) {
        this.lock.lock();
        try {
            action.accept(this.value);
        } finally {
            this.lock.unlock();
        }
    }

    public static <D> Locker<D> of(D value) {
        return new Locker<>(value);
    }

    public static <D> Locker<D> empty() {
        return new Locker<>(null);
    }
}
