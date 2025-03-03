package com.example.routing.requests

import kotlinx.serialization.Serializable


@Serializable
data class UserCreateRequest(
    val username : String,
    val email : String,
    val password : String,
)
