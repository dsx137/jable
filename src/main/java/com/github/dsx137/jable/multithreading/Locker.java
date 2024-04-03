package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.base.Wrapper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <h1>泛型原子类</h1>
 *
 * @param <T> 值的类型
 * @param <L> 锁的类型
 */
public class Locker<T, L extends Lock> {
    private final DelegatingLock<?> lock;
    private final Wrapper<T> value;

    public Locker(T value, L lock) {
        this.value = Wrapper.of(value);
        this.lock = DelegatingLock.of(lock);
    }

    public <R> R compute(Function<Wrapper<T>, R> action) {
        return this.lock.compute(() -> action.apply(this.value));
    }

    public void compute(Consumer<Wrapper<T>> action) {
        this.lock.compute(() -> action.accept(this.value));
    }

    public <R> R tryCompute(Function<Wrapper<T>, R> action) {
        return this.lock.tryCompute(() -> action.apply(this.value));
    }

    public void tryCompute(Consumer<Wrapper<T>> action) {
        this.lock.tryCompute(() -> action.accept(this.value));
    }

    public static <T, L extends Lock> Locker<T, L> of(T value, L lock) {
        return new Locker<>(value, lock);
    }

    public static <T> Locker<T, ReentrantLock> empty() {
        return new Locker<>(null, new ReentrantLock());
    }

    public static <T, L extends Lock> Locker<T, L> empty(L lock) {
        return new Locker<>(null, lock);
    }
}
