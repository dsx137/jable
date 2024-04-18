package com.github.dsx137.jable.extension

fun Boolean?.yes(predicate: () -> Unit): Boolean {
    return this.takeIf { it == true }?.apply { predicate() } ?: false
}

fun Boolean?.no(predicate: () -> Unit): Boolean {
    return this.takeIf { it == false || it == null }?.apply { predicate() } ?: false
}
