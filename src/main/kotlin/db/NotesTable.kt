package com.example.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime


const val MAX_TITLE_COUNT = 50


object NotesTable : IntIdTable() {
    val userId = reference("user_id",UsersTable, onDelete = ReferenceOption.CASCADE)
    val title = varchar(name = "title", length = MAX_TITLE_COUNT)
    val body = text(name = "body")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").nullable()
}