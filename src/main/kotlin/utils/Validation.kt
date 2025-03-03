package com.example.utils

object Validation {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8
    }
}