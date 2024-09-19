package com.github.dsx137.jable.extension

val <T : Any> T.logger: java.util.logging.Logger
    get() = java.util.logging.Logger.getLogger(this::class.java.name)

val <T : Any> T.slf4j: org.slf4j.Logger
    get() = org.slf4j.LoggerFactory.getLogger(this::class.java.name)

val <T : Any> T.log4j: org.apache.logging.log4j.Logger
    get() = org.apache.logging.log4j.LogManager.getLogger(this::class.java.name)