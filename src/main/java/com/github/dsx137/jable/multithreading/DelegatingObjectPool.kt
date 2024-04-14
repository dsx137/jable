package com.github.dsx137.jable.multithreading

import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.GenericObjectPool

/**
 * # 兼容Java的委托对象池
 *
 * 需要Apache Commons Pool2库
 */
class DelegatingObjectPool<T>(factory: PooledObjectFactory<T>) : GenericObjectPool<T>(factory) {
    fun <R> execute(action: T.() -> R): R {
        val o = this.borrowObject()
        return try {
            o.action()
        } finally {
            this.returnObject(o)
        }
    }
}