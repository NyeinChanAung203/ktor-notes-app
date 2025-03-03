package com.example.plugins

import com.example.services.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.get as koinGet

fun Application.configureSecurity() {

    val jwtService: JwtService = koinGet()

    authentication {
        jwt {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)
            validate { jwtCredential ->
                jwtService.customValidate(jwtCredential)
            }
        }
    }
}
