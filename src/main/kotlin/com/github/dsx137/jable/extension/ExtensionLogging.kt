package com.github.dsx137.jable.extension

import java.util.logging.Logger

val <T : Any> T.logger: Logger
    get() = Logger.getLogger(this::class.java.name)

val <T : Any> T.slf4j: org.slf4j.Logger
    get() = org.slf4j.LoggerFactory.getLogger(this::class.java.name)

val <T : Any> T.log4j: org.apache.logging.log4j.Logger
    get() = org.apache.logging.log4j.LogManager.getLogger(this::class.java.name)