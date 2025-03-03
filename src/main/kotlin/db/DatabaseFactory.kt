package com.example.db

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(
        application: Application
    ) {
        connectDatabase(application)
        createTables()
    }

    private fun connectDatabase(
        application: Application
    ) {
        val url = application.environment.config.property("postgres.url").getString()
        val user = application.environment.config.property("postgres.user").getString()
        val password = application.environment.config.property("postgres.password").getString()

        Database.connect(
            url = url,
            user = user,
            password = password
        )
    }

    private fun createTables() {
        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(NotesTable)
            SchemaUtils.create(RefreshTokenTable)
        }
    }


}

suspend fun <T> suspendTransaction(block:  suspend Transaction.() -> T): T =
    newSuspendedTransaction(context = Dispatchers.IO, statement = block)