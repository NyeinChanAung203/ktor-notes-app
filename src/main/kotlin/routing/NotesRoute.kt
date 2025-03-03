package com.example.routing

import com.example.exceptions.AuthException
import com.example.routing.requests.NoteCreateRequest
import com.example.routing.responses.ErrorResponse
import com.example.services.NoteService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Route.notesRoute(
    noteService: NoteService
) {



    authenticate {
        get {
            try {
                val userId = extractUserIdFromPrinciple(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val noteListResponse = noteService.allNotesOfCurrentUser(userId)
                call.respond(noteListResponse)
            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            }
        }

        post {
            try {

                val noteCreateRequest = call.receive<NoteCreateRequest>()
                val userId = extractUserIdFromPrinciple(call)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val noteResponse = noteService.createNote(userId, noteCreateRequest)
                call.respond(noteResponse)

            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            }
        }

        get("{id}") {
            try {
                val userId = extractUserIdFromPrinciple(call)
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest)

                val noteResponse = noteService.noteByIdOfCurrentUser(id, userId) ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("This note is not found."))

                call.respond(HttpStatusCode.OK, noteResponse)


            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            }
        }

        put("{id}") {
            try {
                val userId = extractUserIdFromPrinciple(call)
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val noteCreateRequest = call.receive<NoteCreateRequest>()

                val noteResponse = noteService.updateNote(id = id, userId = userId, noteCreateRequest)
                    ?: return@put call.respond(HttpStatusCode.NotFound, ErrorResponse("This note is not found."))

                call.respond(HttpStatusCode.OK, noteResponse)

            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            }
        }

        delete("{id}") {
            try {
                val userId = extractUserIdFromPrinciple(call)
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)

                val isDeleted = noteService.deleteNote(id = id, userId = userId)
                if (isDeleted) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("This note is not found."))
                }

            } catch (ex: AuthException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            } catch (ex: BadRequestException) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(ex.message.toString()))
            }
        }
    }
}

fun extractUserIdFromPrinciple(
    call: ApplicationCall
): Int? = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString()?.toIntOrNull()