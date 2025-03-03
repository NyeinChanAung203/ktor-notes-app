package com.example.routing.responses

import kotlinx.serialization.Serializable


@Serializable
data class ErrorResponse(
    val message: String,
)
