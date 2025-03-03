package com.example.routing.requests

import kotlinx.serialization.Serializable


@Serializable
data class NoteCreateRequest(
    val title: String,
    val body: String
)
