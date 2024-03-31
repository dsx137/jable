package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.exception.TryComputeException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

/**
 * <h1>委托锁</h1>
 *
 * <p>为锁添加了委托功能</p>
 *
 * @param <T> 锁的类型
 */
public class DelegateLock<T extends Lock> implements Lock {

    @NotNull
    private final T lock;

    public DelegateLock(@NotNull T lock) {
        this.lock = lock;
    }

    @NotNull
    public T getLock() {
        return this.lock;
    }

    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return this.lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return this.lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        this.lock.unlock();
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return this.lock.newCondition();
    }

    public <R> R compute(Supplier<R> action) {
        this.lock.lock();
        R res;
        try {
            res = action.get();
        } finally {
            this.lock.unlock();
        }
        return res;
    }

    public void compute(Runnable action) {
        this.lock.lock();
        try {
            action.run();
        } finally {
            this.lock.unlock();
        }
    }

    public <R> R tryCompute(Supplier<R> action) {
        if (this.lock.tryLock()) {
            throw new TryComputeException();
        }
        R res;
        try {
            res = action.get();
        } finally {
            this.lock.unlock();
        }
        return res;
    }

    public void tryCompute(Runnable action) {
        if (this.lock.tryLock()) {
            throw new TryComputeException();
        }
        try {
            action.run();
        } finally {
            this.lock.unlock();
        }
    }

    public <R> R tryCompute(long time, @NotNull TimeUnit unit, Supplier<R> action) throws InterruptedException {
        if (this.lock.tryLock(time, unit)) {
            throw new TryComputeException();
        }
        R res;
        try {
            res = action.get();
        } finally {
            this.lock.unlock();
        }
        return res;
    }

    public void tryCompute(long time, @NotNull TimeUnit unit, Runnable action) throws InterruptedException {
        if (this.lock.tryLock(time, unit)) {
            throw new TryComputeException();
        }
        try {
            action.run();
        } finally {
            this.lock.unlock();
        }
    }

    public <R> R interruptiblyCompute(Supplier<R> action) throws InterruptedException {
        this.lock.lockInterruptibly();
        R res;
        try {
            res = action.get();
        } finally {
            this.lock.unlock();
        }
        return res;
    }

    public void interruptiblyCompute(Runnable action) throws InterruptedException {
        this.lock.lockInterruptibly();
        try {
            action.run();
        } finally {
            this.lock.unlock();
        }
    }

    public static <D extends Lock> DelegateLock<D> of(D lock) {
        return new DelegateLock<>(lock);
    }
}
