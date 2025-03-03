package com.example.repository

import com.example.db.NotesTable
import com.example.db.suspendTransaction
import com.example.dto.NoteDto
import com.example.dto.toDto
import com.example.entities.NotesEntity
import com.example.entities.UsersEntity
import com.example.exceptions.AuthException
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime

interface NoteRepository {
    suspend fun allNotesOfCurrentUser(userId: Int): List<NoteDto>
    suspend fun createNotesByCurrentUser(
        userId: Int,
        title: String,
        body: String,
    ): NoteDto

    suspend fun getNoteByCurrentUser(id: Int, userId: Int): NoteDto?
    suspend fun updateNotesByCurrentUser(id: Int, userId: Int, title: String, body: String): NoteDto?
    suspend fun deleteNotesByCurrentUser(id: Int, userId: Int): Boolean
}

class PostgresNoteRepository: NoteRepository {

    override suspend fun getNoteByCurrentUser(id: Int, userId: Int): NoteDto? = suspendTransaction {
        NotesEntity.find { (NotesTable.id eq id) and (NotesTable.userId eq userId) }.singleOrNull()?.toDto()
    }

    override suspend fun allNotesOfCurrentUser(userId: Int): List<NoteDto> = suspendTransaction {
        NotesEntity.find { NotesTable.userId eq userId }.toList().map { it.toDto() }
    }

    override suspend fun createNotesByCurrentUser(userId: Int, title: String, body: String): NoteDto =
        suspendTransaction {
            val userEntity = UsersEntity.findById(userId) ?: throw AuthException("Invalid User")
            NotesEntity.new {
                this.userId = userEntity
                this.title = title
                this.body = body
                this.createdAt = LocalDateTime.now()
                this.updatedAt = null
            }.toDto()
        }

    override suspend fun updateNotesByCurrentUser(id: Int, userId: Int, title: String, body: String): NoteDto? =
        suspendTransaction {
            val notesEntity =
                NotesEntity.find { (NotesTable.id eq id) and (NotesTable.userId eq userId) }.singleOrNull()
            notesEntity?.let {
                it.title = title
                it.body = body
                it.updatedAt = LocalDateTime.now()
                it.toDto()
            }
        }

    override suspend fun deleteNotesByCurrentUser(id: Int, userId: Int): Boolean = suspendTransaction {
        val notesEntity = NotesEntity.find { (NotesTable.id eq id) and (NotesTable.userId eq userId) }.singleOrNull()
        if (notesEntity == null) {
            false
        } else {
            notesEntity.delete()
            true
        }

    }
}