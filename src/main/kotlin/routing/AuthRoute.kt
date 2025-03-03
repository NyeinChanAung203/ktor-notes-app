package com.example.routing

import com.example.exceptions.AuthException
import com.example.exceptions.EmailAlreadyExistException
import com.example.exceptions.InvalidEmailException
import com.example.exceptions.NotStrongPasswordException
import com.example.routing.requests.RefreshTokenRequest
import com.example.routing.requests.UserCreateRequest
import com.example.routing.requests.UserSignInRequest
import com.example.routing.responses.ErrorResponse
import com.example.services.AuthService
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.authRoute(
    userService: UserService,
    authService: AuthService
) {


    post("/signUp") {
        try {

            val userCreateRequest = call.receive<UserCreateRequest>()
            val userResponse = userService.createUser(userCreateRequest)
            call.respond(HttpStatusCode.Created, userResponse)


        } catch (ex: EmailAlreadyExistException) {
            call.respond(HttpStatusCode.Conflict, ErrorResponse(ex.message.toString()))
        } catch (ex: InvalidEmailException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
        } catch (ex: NotStrongPasswordException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
        } catch (ex: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }

    post("/signIn") {
        try {

            val userSignInRequest = call.receive<UserSignInRequest>()
            val tokenResponse = authService.authenticate(userSignInRequest) ?: return@post call.respond(
                HttpStatusCode.Unauthorized, ErrorResponse(
                    message = "Email or password is incorrect."
                )
            )

            call.respond(tokenResponse)


        } catch (ex: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: AuthException) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(ex.message.toString()))
        }
    }


    post("/refresh-token") {
        try {

            val refreshTokenRequest = call.receive<RefreshTokenRequest>()
            val tokenResponse = authService.regenerateAccessToken(refreshTokenRequest)
            call.respond(tokenResponse)

        } catch (ex: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest)
        } catch (ex: AuthException) {
            call.respond(HttpStatusCode.Unauthorized, ErrorResponse(ex.message.toString()))
        }
    }

    authenticate {
        post("/logout") {
            try {
                val refreshTokenRequest = call.receive<RefreshTokenRequest>()
                val userId = extractUserIdFromPrinciple(call) ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val isLogout = authService.logout(userId = userId, refreshTokenRequest = refreshTokenRequest)
                if (isLogout) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Token not found."))
                }
            } catch (ex: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest)
            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse(ex.message.toString()))
            }
        }
    }

}