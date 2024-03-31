package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.exception.TryComputeException;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <h1>Id锁</h1>
 *
 * <p>用于对id对应的对象的操作进行加锁</p>
 */
public class IdLocker {

    protected final Map<String, ReentrantLock> idLocks = new ConcurrentHashMap<>();

    protected final ReentrantReadWriteLock metaLock = new ReentrantReadWriteLock();

    protected final DelegateLock<ReentrantLock> rootLockLock = DelegateLock.of(new ReentrantLock());

    protected final ReentrantReadWriteLock.ReadLock idLocksLock = this.metaLock.readLock();

    protected final ReentrantReadWriteLock.WriteLock rootLock = this.metaLock.writeLock();

    /**
     * <h1>计算</h1>
     * <p>对id对应的对象进行操作</p>
     *
     * @param id     id
     * @param action 操作
     * @param <Rr>   返回值类型
     * @return 返回值
     */
    public <Rr> Rr compute(Object id, Supplier<Rr> action) {
        if (id == null) {
            this.rootLockLock.compute(() -> {
                while (this.metaLock.getReadHoldCount() > 0) {
                    this.idLocksLock.unlock();
                }
                this.rootLock.lock();
            });

            try {
                return action.get();
            } finally {
                this.rootLock.unlock();
            }
        } else {
            if (!this.rootLock.isHeldByCurrentThread()) {
                this.idLocksLock.lock();
            }
            ReentrantLock lock = this.idLocks.computeIfAbsent(id.toString(), k -> new ReentrantLock());
            lock.lock();

            try {
                return action.get();
            } finally {
                if (!lock.hasQueuedThreads()) {
                    this.idLocks.remove(id.toString());
                }
                lock.unlock();
                if (this.metaLock.getReadHoldCount() > 0) {
                    this.idLocksLock.unlock();
                }
            }
        }
    }

    public <Rr> Rr compute(Supplier<Rr> action) {
        return this.compute(null, action);
    }

    public void compute(Runnable action) {
        this.compute(() -> {
            action.run();
            return null;
        });
    }

    public void compute(Object id, Runnable action) {
        this.compute(id, () -> {
            action.run();
            return null;
        });
    }

    /**
     * <h1>尝试计算</h1>
     *
     * <p>对id对应的对象进行操作，如果无法获取锁则抛出异常</p>
     *
     * @param id     id
     * @param action 操作
     * @param <Rr>   返回值类型
     * @return 返回值
     */
    public <Rr> Rr tryCompute(Object id, Supplier<Rr> action) {
        if (id == null) {
            this.rootLockLock.compute(() -> {
                while (this.metaLock.getReadHoldCount() > 0) {
                    this.idLocksLock.unlock();
                }
                if (!this.rootLock.tryLock()) {
                    throw new TryComputeException();
                }
            });

            try {
                return action.get();
            } finally {
                this.rootLock.unlock();
            }
        } else {
            if (!this.rootLock.isHeldByCurrentThread()) {
                if (!this.idLocksLock.tryLock()) {
                    throw new TryComputeException();
                }
            }
            ReentrantLock lock = this.idLocks.computeIfAbsent(id.toString(), k -> new ReentrantLock());
            if (!lock.tryLock()) {
                if (this.metaLock.getReadHoldCount() > 0) {
                    this.idLocksLock.unlock();
                }
                throw new TryComputeException();
            }

            try {
                return action.get();
            } finally {
                if (!lock.hasQueuedThreads()) {
                    this.idLocks.remove(id.toString());
                }
                lock.unlock();
                if (this.metaLock.getReadHoldCount() > 0) {
                    this.idLocksLock.unlock();
                }
            }
        }
    }

    public <Rr> Rr tryCompute(Supplier<Rr> action) {
        return this.tryCompute(null, action);
    }

    public void tryCompute(Runnable action) {
        this.tryCompute(() -> {
            action.run();
            return null;
        });
    }

    public void tryCompute(Object id, Runnable action) {
        this.tryCompute(id, () -> {
            action.run();
            return null;
        });
    }

    /**
     * <h1>原子计算链</h1>
     *
     * <p>要同时获取多个锁时请使用链</p>
     */
    public static class ComputeChain {

        private static final long maxWaitTime = 10000;

        private static final double factor = 1.1;

        public static class Builder {
            private final Function<Supplier<?>, ?> function;

            private Builder(Function<Supplier<?>, ?> function) {
                this.function = function;
            }

            public Builder bind(IdLocker idLocker, Object id) {
                return new Builder(f -> this.function.apply(() -> idLocker.tryCompute(id, f)));
            }

            public Builder bind(IdLocker idLocker) {
                return bind(idLocker, null);
            }

            /**
             * <h1>计算</h1>
             *
             * <p>使用循环获取锁，如果无法获取，则释放持有的锁然后continue</p>
             * <p>使用指数退避算法</p>
             *
             * @param action 操作
             */
            @SuppressWarnings("unchecked")
            public <R> R compute(Supplier<R> action) {
                int retries = 0;
                Random random = new Random();
                while (true) {
                    try {
                        return (R) this.function.apply(action);
                    } catch (TryComputeException ignored) {
                        try {
                            int rawWaitTime = (int) Math.min(maxWaitTime, Math.pow(factor, retries));
                            int waitTime = rawWaitTime + random.nextInt(rawWaitTime);
                            retries++;
                            Thread.sleep(waitTime);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Thread interrupted", e);
                        }
                    }
                }
            }

            public void compute(Runnable action) {
                this.compute(() -> {
                    action.run();
                    return null;
                });
            }
        }

        private ComputeChain() {
        }

        public static Builder bind(IdLocker idLocker, Object id) {
            return new Builder(f -> idLocker.tryCompute(id, f));
        }

        public static Builder bind(IdLocker idLocker) {
            return bind(idLocker, null);
        }
    }
}
