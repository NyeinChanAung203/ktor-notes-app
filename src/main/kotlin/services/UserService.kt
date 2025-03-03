package com.example.services

import com.example.exceptions.EmailAlreadyExistException
import com.example.exceptions.InvalidEmailException
import com.example.exceptions.NotStrongPasswordException
import com.example.repository.UserRepository
import com.example.routing.requests.UserCreateRequest
import com.example.routing.responses.UserResponse
import com.example.routing.responses.toUserResponse
import com.example.utils.Validation
import com.example.utils.hashPassword


class UserService(
    private val userRepository: UserRepository
) {
    suspend fun createUser(
        userCreateRequest: UserCreateRequest
    ): UserResponse {
        if (!Validation.isValidEmail(userCreateRequest.email)) {
            throw InvalidEmailException("Email is invalid.")
        }

        val isEmailExisted = userRepository.isEmailExists(userCreateRequest.email)
        if (isEmailExisted) {
            throw EmailAlreadyExistException("This email is already existed.")
        }

        if (!Validation.isStrongPassword(userCreateRequest.password)) {
            throw NotStrongPasswordException("Password is weak.")
        }

        val userDto = userRepository.createUser(
            username = userCreateRequest.username,
            password = hashPassword(userCreateRequest.password),
            email = userCreateRequest.email
        )
        return userDto.toUserResponse()
    }


}