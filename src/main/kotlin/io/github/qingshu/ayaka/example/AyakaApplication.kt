package io.github.qingshu.ayaka.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
class ExampleAyakaApplication

fun main(args: Array<String>) {
    runApplication<ExampleAyakaApplication>(*args)
}
