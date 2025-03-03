package com.example.routing

import com.example.routing.responses.ErrorResponse
import com.example.services.AuthService
import com.example.services.NoteService
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(message = ErrorResponse(cause.toString()) , status = HttpStatusCode.InternalServerError)
        }
    }

    val userService: UserService = get()
    val authService: AuthService = get()
    val noteService: NoteService = get()


    routing {

        route("/api/auth") {
            authRoute(userService,authService)
        }

        route("/api/notes") {
            notesRoute(noteService)
        }

    }
}
