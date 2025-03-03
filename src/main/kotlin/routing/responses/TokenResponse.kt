package com.example.routing.responses

import kotlinx.serialization.Serializable


@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiredAt: String
)
