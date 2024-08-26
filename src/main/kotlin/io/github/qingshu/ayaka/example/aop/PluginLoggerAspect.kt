package io.github.qingshu.ayaka.example.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Aspect
@Component
class PluginLoggerAspect {

    @Pointcut("execution(* io.github.qingshu.ayaka.example.plugin..*(..)) && this(io.github.qingshu.ayaka.plugin.BotPlugin)")
    fun allMethodsInBotPluginImplements() {
    }

    @Around("allMethodsInBotPluginImplements()")
    fun callLogger(joinPoint: ProceedingJoinPoint): Any? {
        try {
            val methodName = joinPoint.signature.name
            log.info("Method {} executed", methodName)
            return joinPoint.proceed()
        } catch (e: Throwable) {
            log.error("Process error")
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(PluginLoggerAspect::class.java)
    }
}