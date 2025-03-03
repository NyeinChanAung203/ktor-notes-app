package com.example.repository

import com.example.db.RefreshTokenTable
import com.example.db.suspendTransaction
import com.example.dto.RefreshTokenDto
import com.example.dto.toDto
import com.example.entities.RefreshTokenEntity
import com.example.entities.UsersEntity
import com.example.exceptions.AuthException
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime


interface RefreshTokenRepository {
    suspend fun getToken(refreshToken: String): RefreshTokenDto?
    suspend fun getTokenByUserId(userId: Int): RefreshTokenDto?
    suspend fun createToken(userId: Int, token: String, expiredAt: LocalDateTime): RefreshTokenDto
    suspend fun updateToken(
        userId: Int,
        token: String,
        expiredAt: LocalDateTime
    ): RefreshTokenDto?

    suspend fun deleteToken(userId: Int, refreshToken: String): Boolean
}

class PostgresRefreshTokenRepository : RefreshTokenRepository {
    override suspend fun getToken(refreshToken: String): RefreshTokenDto? = suspendTransaction {
        RefreshTokenEntity.find { RefreshTokenTable.token eq refreshToken }
            .singleOrNull()?.toDto()
    }

    override suspend fun getTokenByUserId(userId: Int): RefreshTokenDto? = suspendTransaction {
        RefreshTokenEntity.find { RefreshTokenTable.userId eq userId }
            .singleOrNull()?.toDto()
    }

    override suspend fun createToken(
        userId: Int,
        token: String,
        expiredAt: LocalDateTime
    ): RefreshTokenDto =
        suspendTransaction {
            val userEntity = UsersEntity.findById(userId) ?: throw  AuthException("Invalid User.")
            RefreshTokenEntity.new {
                this.userId = userEntity
                this.token = token
                this.expiredAt = expiredAt
            }.toDto()
        }

    override suspend fun updateToken(
        userId: Int,
        token: String,
        expiredAt: LocalDateTime
    ): RefreshTokenDto? = suspendTransaction {
        val refreshTokenEntity = RefreshTokenEntity.find{ RefreshTokenTable.userId eq userId }.singleOrNull()
        refreshTokenEntity?.let {
            it.token = token
            it.expiredAt = expiredAt
            it.toDto()
        }
    }

    override suspend fun deleteToken(userId: Int, refreshToken: String): Boolean = suspendTransaction {
        val refreshTokenEntity =
            RefreshTokenEntity.find { (RefreshTokenTable.token eq refreshToken) and (RefreshTokenTable.userId eq userId) }
                .singleOrNull()
        if (refreshTokenEntity == null) {
            false
        } else {
            refreshTokenEntity.delete()
            true
        }
    }
}