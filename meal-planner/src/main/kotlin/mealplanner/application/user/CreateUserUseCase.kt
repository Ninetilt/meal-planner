package de.dhbw.mealplanner.application.user

import de.dhbw.mealplanner.application.user.commands.CreateUserCommand
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(name: String, email: String, password: String): UserId =
        execute(
            CreateUserCommand(
                name = name,
                email = email,
                password = password
            )
        )

    fun execute(command: CreateUserCommand): UserId {
        if (command.name.isBlank()) throw ValidationError("name must not be blank")

        if (command.password.isBlank()) throw ValidationError("password must not be blank")

        val emailAddress = try {
            EmailAddress(command.email.trim())
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid email")
        }

        val existingUser = userRepository.findByEmail(emailAddress)
        if (existingUser != null) throw ValidationError("email already exists")

        val user = User(
            id = UserId(UUID.randomUUID()),
            name = command.name.trim(),
            email = emailAddress,
            password = command.password
        )

        userRepository.save(user)
        return user.id
    }
}
