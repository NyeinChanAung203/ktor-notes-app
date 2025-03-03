package com.example.routing.responses

import kotlinx.serialization.Serializable


@Serializable
data class NoteListResponse(
    val notes: List<NoteResponse>
)
