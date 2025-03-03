package com.example.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokenTable: IntIdTable() {
    val userId = reference("user_id",UsersTable,ReferenceOption.CASCADE)
    val token = varchar("token",512)
    val expiredAt = datetime("expired_at")
}