package com.github.dsx137.jable.misc

import java.util.logging.Logger
import java.util.stream.Stream

fun Boolean?.yes(predicate: () -> Unit): Boolean {
    return this.takeIf { it == true }?.apply { predicate() } ?: false
}

fun Boolean?.no(predicate: () -> Unit): Boolean {
    return this.takeIf { it == false || it == null }?.apply { predicate() } ?: false
}

fun Boolean?.yesStrict(predicate: () -> Unit): Boolean? {
    return this.takeIf { it == true }?.apply { predicate() }
}

fun Boolean?.noStrict(predicate: () -> Unit): Boolean? {
    return this.takeIf { it == false }?.apply { predicate() }
}

fun <E, T : Iterable<E>> T.forEachThen(predicate: (E) -> Unit): T {
    this.forEach(predicate)
    return this
}

fun <T> Stream<T?>.filterNotNull(): Stream<T> {
    return this.filter { it != null }.map { it!! }
}

val <T : Any> T.logger: Logger
    get() = Logger.getLogger(this::class.java.name)
