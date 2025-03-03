package com.example.dto

import com.example.entities.UsersEntity
import java.time.LocalDateTime

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val createdAt: LocalDateTime
)


fun UsersEntity.toDto() = UserDto(
    id = this.id.value,
    email = this.email,
    username = this.username,
    password = this.password,
    createdAt = this.createdAt,
)