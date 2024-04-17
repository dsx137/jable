package com.github.dsx137.jable.extension

import java.util.stream.Stream

fun <E, T : Iterable<E>> T.forEachThen(predicate: (E) -> Unit): T {
    this.forEach(predicate)
    return this
}

fun <T> Stream<T?>.filterNotNull(): Stream<T> {
    return this.filter { it != null }.map { it!! }
}