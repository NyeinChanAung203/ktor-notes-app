package com.example.routing.responses

import com.example.dto.UserDto
import com.example.entities.UsersEntity
import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val created: String
)


fun UserDto.toUserResponse() = UserResponse(
    id = this.id,
    username = this.username,
    email = this.email,
    created = this.createdAt.toString()
)