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
class LoggerAspect {

    @Pointcut("execution(* io.github.qingshu.ayaka.example.plugin..*(..)) && this(io.github.qingshu.ayaka.plugin.BotPlugin)")
    fun pluginCall() {
    }

    @Pointcut("execution(* io.github.qingshu.ayaka.example.function..*.apply(..))")
    fun functionCall(){}

    @Around("functionCall() || pluginCall()")
    fun applyMethodInFunction(joinPoint: ProceedingJoinPoint): Any {
        val startTime = System.currentTimeMillis()
        return try {
            val className = joinPoint.signature.declaringType.simpleName
            val methodName = joinPoint.signature.name
            log.info("$className.$methodName started")
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            log.info("$className.$methodName executed in $duration ms")
            result ?: Any()
        } catch (e: Throwable) {
            log.error("Process error ${e.message}")
            return Any()
        }
    }


    companion object {
        private val log = LoggerFactory.getLogger(LoggerAspect::class.java)
    }
}