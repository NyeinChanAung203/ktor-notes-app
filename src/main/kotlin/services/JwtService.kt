package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.utils.expireTimeOfAccessToken
import com.example.utils.expireTimeOfRefreshToken
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*

class JwtService(
    private val application: Application,
) {

    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    private val secret = getConfigProperty("jwt.secret")

    val realm = getConfigProperty("jwt.realm")


    val jwtVerifier: JWTVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()


    fun createAccessToken(id: String, email: String): String = JWT
        .create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("id", id)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + expireTimeOfAccessToken))
        .sign(Algorithm.HMAC256(secret))

    fun createRefreshToken(id: String): String = JWT
        .create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("id", id)
        .withExpiresAt(Date(System.currentTimeMillis() + expireTimeOfRefreshToken))
        .sign(Algorithm.HMAC256(secret))


    fun customValidate(jwtCredential: JWTCredential): JWTPrincipal? {
        jwtCredential.payload.getClaim("id").asString().toIntOrNull() ?: return null

        return if (audienceMatches(jwtCredential)) {
            JWTPrincipal(jwtCredential.payload)
        } else null

    }

    private fun audienceMatches(credential: JWTCredential): Boolean =
        credential.payload.audience.contains(audience)

    private fun getConfigProperty(path: String): String =
        application.environment.config.property(path).getString()


}