package com.github.dsx137.jable.multithreading

import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.GenericObjectPool
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * # 兼容Java的委托对象池
 *
 * 需要Apache Commons Pool2库
 */
class DelegateObjectPool<T>(factory: PooledObjectFactory<T>) : GenericObjectPool<T>(factory) {
    @OptIn(ExperimentalContracts::class)
    fun <R> execute(action: T.() -> R): R {
        contract {
            callsInPlace(action, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        }
        val o = this.borrowObject()
        return try {
            o.action()
        } finally {
            this.returnObject(o)
        }
    }
}