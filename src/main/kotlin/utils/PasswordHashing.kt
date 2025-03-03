package com.example.utils

import org.mindrot.jbcrypt.BCrypt

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt()) // Generates a salt and hashes the password
}


fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
    return BCrypt.checkpw(plainPassword, hashedPassword)
}
