package com.example.dto

import com.example.entities.RefreshTokenEntity
import java.time.LocalDateTime

data class RefreshTokenDto(
    val id: Int,
    val token: String,
    val expiredAt: LocalDateTime,
    val userDto: UserDto,
)

fun RefreshTokenEntity.toDto() = RefreshTokenDto(
    id = this.id.value,
    token = this.token,
    expiredAt = this.expiredAt,
    userDto = this.userId.toDto()
)