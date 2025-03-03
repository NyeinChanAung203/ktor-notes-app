package com.example.routing.responses

import com.example.dto.NoteDto
import kotlinx.serialization.Serializable


@Serializable
data class NoteResponse(
    val id: Int,
    val title: String,
    val body: String,
    val createdAt: String,
    val updatedAt: String?,
    val user: UserResponse
)

fun NoteDto.toNoteResponse() = NoteResponse(
    id = this.id,
    title = this.title,
    body = this.body,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt?.toString(),
    user = this.userDto.toUserResponse()
)
