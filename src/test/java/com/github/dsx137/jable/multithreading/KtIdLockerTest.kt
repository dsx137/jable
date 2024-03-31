package com.github.dsx137.jable.multithreading

class KtIdLockerTest {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val idLocker = IdLocker()
            val number = idLocker.compute {
                println("1")
                1
            }
        }
    }
}