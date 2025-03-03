package com.example.plugins

import com.example.db.DatabaseFactory
import io.ktor.server.application.*

fun Application.configureDatabases() {
    DatabaseFactory.init(this);
}