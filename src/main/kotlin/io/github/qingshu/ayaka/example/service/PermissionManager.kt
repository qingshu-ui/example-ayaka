package io.github.qingshu.ayaka.example.service

import io.github.qingshu.ayaka.dto.event.message.MessageEvent
import org.springframework.stereotype.Service

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the AGPL-3.0 License.
 * See the LICENSE file for details.
 */
@Service
class PermissionManager {

    private val cmdPermission: MutableMap<String, (Long) -> Boolean> = mutableMapOf()
    private val denyHandler: MutableMap<String, (MessageEvent) -> Unit> = mutableMapOf()
    private val grantHandler: MutableMap<String, (MessageEvent) -> Unit> = mutableMapOf()

    fun isRegistered(cmd: String) = cmd in cmdPermission

    fun register(cmd: String, setup: PermissionRule.() -> Unit): PermissionManager {
        val rule = PermissionRule().apply(setup)
        denyHandler[cmd] = rule.denyHandler
        cmdPermission[cmd] = rule.checker
        grantHandler[cmd] = rule.grantHandler
        return this
    }

    fun register(cmd: String, rule: PermissionRule) = register(cmd) {
        denyHandler = rule.denyHandler
        grantHandler = rule.grantHandler
        checker = rule.checker
    }

    fun grantHandler(cmd: String, handler: (MessageEvent) -> Unit) {
        grantHandler[cmd] = handler
    }

    fun denyHandler(cmd: String, handler: (MessageEvent) -> Unit) {
        denyHandler[cmd] = handler
    }

    fun checker(cmd: String, checker: (Long) -> Boolean) {
        cmdPermission[cmd] = checker
    }

    fun hasPermission(cmd: String, event: MessageEvent): Boolean {
        val userId = event.userId
        val hasPermission = cmdPermission[cmd]?.invoke(userId) ?: true
        if (!hasPermission) {
            denyHandler[cmd]?.invoke(event)
        } else {
            grantHandler[cmd]?.invoke(event)
        }
        return hasPermission
    }

    fun execute(cmd: String, event: MessageEvent) = hasPermission(cmd, event)

    class PermissionRule {
        var denyHandler: (MessageEvent) -> Unit = { }
        var grantHandler: (MessageEvent) -> Unit = { }
        var checker: (Long) -> Boolean = { true }
    }
}