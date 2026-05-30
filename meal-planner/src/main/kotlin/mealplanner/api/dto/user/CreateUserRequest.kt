package de.dhbw.mealplanner.api.dto.user

import de.dhbw.mealplanner.application.user.commands.CreateUserCommand
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
