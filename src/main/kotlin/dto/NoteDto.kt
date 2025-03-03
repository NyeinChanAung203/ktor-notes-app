package com.example.dto

import com.example.db.NotesTable
import com.example.entities.NotesEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import java.time.LocalDateTime

data class NoteDto(
    val id: Int,
    val title: String,
    val body: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?,
    val userDto: UserDto
)


fun NotesEntity.toDto() = NoteDto(
    id = this.id.value,
    title = this.title,
    body = this.body,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    userDto = this.userId.toDto()
)

