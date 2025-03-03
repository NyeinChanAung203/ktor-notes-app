package com.example.routing.requests

import kotlinx.serialization.Serializable


@Serializable
data class RefreshTokenRequest(
    val token: String
)
