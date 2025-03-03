package com.example.plugins

import com.example.appModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencies() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
        properties(mapOf("application" to this@configureDependencies))
    }
}