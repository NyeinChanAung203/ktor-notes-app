package com.example.repository

import com.example.db.UsersTable
import com.example.db.suspendTransaction
import com.example.dto.UserDto
import com.example.dto.toDto
import com.example.entities.UsersEntity
import java.time.LocalDateTime

interface UserRepository {
    suspend fun createUser(username: String, email: String, password: String): UserDto
    suspend fun isEmailExists(email: String): Boolean
    suspend fun deleteUser(id: Int): Boolean
    suspend fun getUserById(id: Int): UserDto?
    suspend fun getUserByEmail(email: String): UserDto?
}

class PostgresUserRepository : UserRepository {
    override suspend fun getUserByEmail(email: String): UserDto? =
        suspendTransaction {
            UsersEntity
                .find { UsersTable.email eq email }
                .singleOrNull()?.toDto()
        }

    override suspend fun getUserById(id: Int): UserDto? = suspendTransaction {
        UsersEntity.findById(id)?.toDto()
    }

    override suspend fun isEmailExists(email: String): Boolean = suspendTransaction {
        UsersEntity.find { UsersTable.email eq email }.singleOrNull() != null
    }

    override suspend fun createUser(username: String, email: String, password: String): UserDto =
        suspendTransaction {
            UsersEntity.new {
                this.username = username
                this.password = password
                this.email = email
                this.createdAt = LocalDateTime.now()
            }.toDto()
        }

    override suspend fun deleteUser(id: Int): Boolean = suspendTransaction {
        val foundUser = UsersEntity.findById(id)
        if (foundUser != null) {
            foundUser.delete()
            true
        } else false
    }

}