package com.example.services

import com.example.exceptions.AuthException
import com.example.repository.NoteRepository
import com.example.repository.UserRepository
import com.example.routing.requests.NoteCreateRequest
import com.example.routing.responses.NoteListResponse
import com.example.routing.responses.NoteResponse
import com.example.routing.responses.toNoteResponse


class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
) {
    suspend fun noteByIdOfCurrentUser(id: Int, userId: Int): NoteResponse? {
        return noteRepository.getNoteByCurrentUser(id, userId)?.toNoteResponse()
    }

    suspend fun allNotesOfCurrentUser(userId: Int): NoteListResponse {
        return NoteListResponse(
            noteRepository
                .allNotesOfCurrentUser(userId)
                .toList().map { it.toNoteResponse() }
        )
    }

    suspend fun createNote(userId: Int, noteCreateRequest: NoteCreateRequest): NoteResponse {
        val userDto = userRepository.getUserById(userId) ?: throw AuthException("User is invalid.")
        val notesEntity = noteRepository.createNotesByCurrentUser(
            userDto.id,
            title = noteCreateRequest.title,
            body = noteCreateRequest.body,
        )
        return notesEntity.toNoteResponse()
    }

    suspend fun updateNote(id: Int, userId: Int, noteCreateRequest: NoteCreateRequest): NoteResponse? {
        val notesEntity = noteRepository.updateNotesByCurrentUser(
            id, userId, noteCreateRequest.title, noteCreateRequest.body
        ) ?: return null
        return notesEntity.toNoteResponse()
    }

    suspend fun deleteNote(id: Int, userId: Int): Boolean {
        return noteRepository.deleteNotesByCurrentUser(id, userId)
    }
}