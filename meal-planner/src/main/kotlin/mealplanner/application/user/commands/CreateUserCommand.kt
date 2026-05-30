package de.dhbw.mealplanner.application.user.commands

data class CreateUserCommand(
    val name: String,
    val email: String,
    val password: String
)