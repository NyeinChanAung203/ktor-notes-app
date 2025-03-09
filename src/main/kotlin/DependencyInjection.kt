package com.example

import com.example.repository.*
import com.example.services.AuthService
import com.example.services.JwtService
import com.example.services.NoteService
import com.example.services.UserService
import io.ktor.server.application.*
import org.koin.dsl.module


val appModule = module {
    single { getProperty<Application>("application") }
    single<UserRepository> { PostgresUserRepository() }
    single<RefreshTokenRepository> { PostgresRefreshTokenRepository() }
    single<NoteRepository> { PostgresNoteRepository() }

    single { JwtService(get()) }
    single { AuthService(get(), get(), get()) }
    single { UserService(get()) }
    single { NoteService(get(), get()) }
}

