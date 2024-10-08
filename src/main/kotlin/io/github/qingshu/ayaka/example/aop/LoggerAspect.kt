package io.github.qingshu.ayaka.example.aop

import io.github.qingshu.ayaka.example.annotation.Slf4j
import io.github.qingshu.ayaka.example.annotation.Slf4j.Companion.log
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Slf4j
@Aspect
@Component
class LoggerAspect {

    @Pointcut("@annotation(meteordevelopment.orbit.EventHandler)")
    fun pluginCall() {
    }

    @Around("pluginCall()")
    fun applyMethodInFunction(joinPoint: ProceedingJoinPoint): Any {
        val className = joinPoint.signature.declaringType.simpleName
        val methodName = joinPoint.signature.name
        val startTime = System.currentTimeMillis()
        return try {
            log.info("$className.$methodName started")
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            log.info("$className.$methodName executed in $duration ms")
            result ?: Any()
        } catch (e: Throwable) {
            log.error("$className.$methodName caught error ${e.javaClass.simpleName}: ${e.message}")
            return Any()
        }
    }

}