package com.github.dsx137.jable.multithreading

import com.github.dsx137.jable.exception.TryComputeException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlin.math.min
import kotlin.math.pow

/**
 * <h1>Id锁</h1>
 *
 * <p>用于对id对应的对象的操作进行加锁</p>
 */
open class IdLocker {

    private val idLocks: ConcurrentHashMap<String, ReentrantLock> = ConcurrentHashMap()

    private val metaLock = ReentrantReadWriteLock()

    private val rootLockLock = ReentrantLock()

    private val idLocksLock = this.metaLock.readLock()

    private val rootLock = this.metaLock.writeLock()


    /**
     * # 计算
     *
     * 对id对应的对象进行操作
     *
     * @param id     id
     * @param action 操作
     * @param <Rr>   返回值类型
     * @return 返回值
     */
    open fun <R> compute(id: Any?, action: () -> R): R {
        if (id == null) {
            this.rootLockLock.withLock {
                while (this.metaLock.readHoldCount > 0) {
                    this.idLocksLock.unlock()
                }
                this.rootLock.lock()
            }

            try {
                return action()
            } finally {
                this.rootLock.unlock()
            }
        } else {
            if (!this.rootLock.isHeldByCurrentThread) {
                this.idLocksLock.lock()
            }
            val lock = this.idLocks.computeIfAbsent(id.toString()) { ReentrantLock() }
            lock.lock()

            try {
                return action()
            } finally {
                if (!lock.hasQueuedThreads()) {
                    this.idLocks.remove(id.toString())
                }
                lock.unlock()
                if (this.metaLock.readHoldCount > 0) {
                    this.idLocksLock.unlock()
                }
            }
        }
    }

    open fun <R> compute(action: () -> R): R {
        return this.compute(null) { action.invoke() }
    }

    open fun compute(id: Any?, action: Runnable) {
        this.compute(id) { action.run() }
    }

    open fun compute(action: Runnable) {
        this.compute(null) { action.run() }
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
    open fun <R> tryCompute(id: Any?, action: () -> R): R {
        if (id == null) {
            this.rootLockLock.withLock {
                while (this.metaLock.readHoldCount > 0) {
                    this.idLocksLock.unlock()
                }
                if (!this.rootLock.tryLock()) {
                    throw TryComputeException()
                }
            }

            try {
                return action()
            } finally {
                this.rootLock.unlock()
            }
        } else {
            if (!this.rootLock.isHeldByCurrentThread) {
                if (!this.idLocksLock.tryLock()) {
                    throw TryComputeException()
                }
            }
            val lock = this.idLocks.computeIfAbsent(id.toString()) { ReentrantLock() }
            if (!lock.tryLock()) {
                if (this.metaLock.readHoldCount > 0) {
                    this.idLocksLock.unlock()
                }
                throw TryComputeException()
            }

            try {
                return action()
            } finally {
                if (!lock.hasQueuedThreads()) {
                    this.idLocks.remove(id.toString())
                }
                lock.unlock()
                if (this.metaLock.readHoldCount > 0) {
                    this.idLocksLock.unlock()
                }
            }
        }
    }

    open fun <R> tryCompute(action: () -> R): R {
        return this.tryCompute(null) { action.invoke() }
    }

    open fun tryCompute(id: Any?, action: Runnable) {
        this.tryCompute(id) { action.run() }
    }

    open fun tryCompute(action: Runnable) {
        this.tryCompute { action.run() }
    }

    /**
     * <h1>原子计算链</h1>
     *
     * <p>要同时获取多个锁时请使用链</p>
     */
    open class ComputeChain private constructor() {
        open class Builder internal constructor(
            private var function: (() -> Any?) -> Any?,
        ) {

            open fun bind(idLocker: IdLocker, id: Any?): Builder {
                return Builder { f -> this.function { idLocker.tryCompute(id, f) } }
            }

            open fun bind(idLocker: IdLocker): Builder {
                return bind(idLocker, null)
            }

            /**
             * <h1>计算</h1>
             *
             * <p>使用循环获取锁，如果无法获取，则释放持有的锁然后continue</p>
             * <p>使用指数退避算法</p>
             *
             * @param action 操作
             */
            @Suppress("UNCHECKED_CAST")
            open fun <R> compute(action: () -> R): R {
                var retries = 0
                val random = ThreadLocalRandom.current()
                while (true) {
                    try {
                        return this.function(action) as R
                    } catch (ignored: TryComputeException) {
                        try {
                            val rawWaitTime = min(MAX_WAIT_TIME, FACTOR.pow(retries).toInt())
                            val waitTime = rawWaitTime + random.nextInt(rawWaitTime)
                            retries++
                            Thread.sleep(waitTime.toLong())
                        } catch (e: InterruptedException) {
                            Thread.currentThread().interrupt()
                            throw RuntimeException("Thread interrupted", e)
                        }
                    }
                }
            }

            open fun compute(action: Runnable) {
                this.compute { action.run() }
            }

            companion object {
                private const val MAX_WAIT_TIME = 10000
                private const val FACTOR = 1.1
            }
        }

        companion object {
            @JvmStatic
            fun bind(idLocker: IdLocker, id: Any?): Builder {
                return Builder { f -> idLocker.compute(id, f) }
            }

            @JvmStatic
            fun bind(idLocker: IdLocker): Builder {
                return bind(idLocker, null)
            }
        }
    }
}
