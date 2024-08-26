package io.github.qingshu.ayaka.example.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * Copyright (c) 2024 qingshu.
 * This file is part of the example-ayaka project.
 *
 * This project is licensed under the GPL-3.0 License.
 * See the LICENSE file for details.
 */
@Controller
class HomePage {

    @RequestMapping("/")
    fun index(): String{
        return "index"
    }
}