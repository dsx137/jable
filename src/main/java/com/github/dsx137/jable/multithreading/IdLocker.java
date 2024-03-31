package com.github.dsx137.jable.multithreading;

import com.github.dsx137.jable.exception.TryComputeException;
import com.github.dsx137.jable.misc.Useless;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class IdLocker {

    protected final Map<String, ReentrantLock> idLocks = new ConcurrentHashMap<>();

    protected final ReentrantReadWriteLock metaLock = new ReentrantReadWriteLock();

    protected final DelegateLock<ReentrantLock> rootLockLock = DelegateLock.of(new ReentrantLock());

    protected final ReentrantReadWriteLock.ReadLock idLocksLock = this.metaLock.readLock();

    protected final ReentrantReadWriteLock.WriteLock rootLock = this.metaLock.writeLock();

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
            if (lock.tryLock()) {
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

            @SuppressWarnings("unchecked")
            public <R> R compute(Supplier<R> action) {
                return (R) this.function.apply(action);
            }

            public void compute(Runnable action) {
                boolean isSuccessful = false;

                while (!isSuccessful) {
                    try {
                        this.function.apply(() -> {
                            action.run();
                            return null;
                        });
                        isSuccessful = true;
                    } catch (TryComputeException ignored) {
                        Useless.useless();
                    }
                }
            }
        }

        private ComputeChain() {
        }

        public static Builder bind(IdLocker idLocker, Object id) {
            return new Builder(f -> idLocker.compute(id, f));
        }

        public static Builder bind(IdLocker idLocker) {
            return bind(idLocker, null);
        }
    }
}
