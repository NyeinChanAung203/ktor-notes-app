package com.example.routing.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserSignInRequest(
    val email: String,
    val password: String
)
