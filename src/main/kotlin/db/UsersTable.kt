package com.example.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable: IntIdTable() {
    val username = varchar("username", length = 50)
    val email = varchar("email", length = 50).uniqueIndex()
    val password = varchar("password", length = 512)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
