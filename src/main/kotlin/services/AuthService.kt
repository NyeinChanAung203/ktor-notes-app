package com.example.services

import com.example.dto.UserDto
import com.example.exceptions.AuthException
import com.example.repository.RefreshTokenRepository
import com.example.repository.UserRepository
import com.example.routing.requests.RefreshTokenRequest
import com.example.routing.requests.UserSignInRequest
import com.example.routing.responses.TokenResponse
import com.example.utils.expireTimeOfAccessToken
import com.example.utils.expireTimeOfRefreshToken
import com.example.utils.verifyPassword
import io.ktor.server.util.*
import io.ktor.utils.io.*
import java.time.LocalDateTime
import java.util.*

class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JwtService
) {

    @OptIn(InternalAPI::class)
    suspend fun authenticate(
        userSignInRequest: UserSignInRequest
    ): TokenResponse? {

        val userDto = userRepository
            .getUserByEmail(userSignInRequest.email)

        return userDto?.let {
            val hashedPasswordFromDb = it.password
            if (verifyPassword(userSignInRequest.password, hashedPasswordFromDb)) {
                saveRefreshToken(it)

            } else null

        }
    }

    @OptIn(InternalAPI::class)
    suspend fun regenerateAccessToken(
        refreshTokenRequest: RefreshTokenRequest
    ): TokenResponse {
        val refreshTokenDto =
            refreshTokenRepository.getToken(refreshTokenRequest.token) ?: throw AuthException("Invalid Refresh Token")

        if (refreshTokenDto.expiredAt.isBefore(LocalDateTime.now())) {
            println("token is expired")
            throw AuthException("Refresh Token is expired.")
        }

        val userId = refreshTokenDto.userDto.id

        val accessToken = jwtService.createAccessToken(userId.toString(), refreshTokenDto.userDto.email)
        val refreshToken = jwtService.createRefreshToken(userId.toString())
        val expiredAtOfRefreshToken = Date(System.currentTimeMillis() + expireTimeOfRefreshToken).toLocalDateTime()
        val expiredAtOfAccessToken = Date(System.currentTimeMillis() + expireTimeOfAccessToken).toLocalDateTime()


        refreshTokenRepository.updateToken(userId, refreshToken, expiredAtOfRefreshToken)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiredAt = expiredAtOfAccessToken.toString(),
        )
    }


    @OptIn(InternalAPI::class)
    private suspend fun saveRefreshToken(
        userDto: UserDto,
    ): TokenResponse {

        val userId = userDto.id

        // check token is in db
        val refreshTokenEntity = refreshTokenRepository.getTokenByUserId(userId)

        val accessToken = jwtService.createAccessToken(userId.toString(), userDto.email)
        val refreshToken = jwtService.createRefreshToken(userId.toString())
        val expiredAtOfRefreshToken = Date(System.currentTimeMillis() + expireTimeOfRefreshToken).toLocalDateTime()
        val expiredAtOfAccessToken = Date(System.currentTimeMillis() + expireTimeOfAccessToken).toLocalDateTime()
        if (refreshTokenEntity == null) {
            // create
            refreshTokenRepository.createToken(userId, refreshToken, expiredAtOfRefreshToken)

            return TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiredAt = expiredAtOfAccessToken.toString(),
            )
        } else {
            // update

            refreshTokenRepository.updateToken(userId, refreshToken, expiredAtOfRefreshToken)

            return TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiredAt = expiredAtOfAccessToken.toString(),
            )
        }

    }

    suspend fun logout(userId: Int,refreshTokenRequest: RefreshTokenRequest): Boolean {
        return refreshTokenRepository.deleteToken(userId, refreshTokenRequest.token)
    }
}