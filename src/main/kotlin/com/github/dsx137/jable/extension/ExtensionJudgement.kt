package com.github.dsx137.jable.extension

fun Boolean.yes(predicate: () -> Unit): Boolean = this.also { if (it) predicate() }
fun Boolean.no(predicate: () -> Unit): Boolean = this.also { if (!it) predicate() }

infix fun Boolean.nor(boolean: Boolean): Boolean = !(this || boolean)
infix fun Boolean.nand(boolean: Boolean): Boolean = !(this && boolean)
infix fun Boolean.xnor(boolean: Boolean): Boolean = this == boolean
