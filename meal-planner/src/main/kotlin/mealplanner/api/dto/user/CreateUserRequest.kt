package de.dhbw.mealplanner.api.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String
) {
    fun toCommand() = CreateUserCommand(
        name = name,
        email = email,
        password = password
    )
}

data class CreateUserCommand(
    val name: String,
    val email: String,
    val password: String
)
