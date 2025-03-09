package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
        val url = System.getenv("DATABASE_URL") ?: application.environment.config.property("postgres.url").getString()
        val username = System.getenv("DATABASE_USERNAME") ?: application.environment.config.property("postgres.user").getString()
        val password = System.getenv("DATABASE_PASSWORD") ?: application.environment.config.property("postgres.password").getString()

        val config = HikariConfig().apply {
            jdbcUrl = url
            this.username = username
            this.password = password
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

    }

    private fun createTables() {
        transaction {
            SchemaUtils.create(UsersTable)
            SchemaUtils.create(NotesTable)
            SchemaUtils.create(RefreshTokenTable)
        }
    }


}

suspend fun <T> suspendTransaction(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(context = Dispatchers.IO, statement = block)