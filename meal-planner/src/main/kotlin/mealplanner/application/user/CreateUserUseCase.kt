package de.dhbw.mealplanner.application.user

import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(name: String, email: String, password: String): UserId {
        if (name.isBlank()) throw ValidationError("name must not be blank")

        if (password.isBlank()) throw ValidationError("password must not be blank")

        val emailAddress = try {
            EmailAddress(email.trim())
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid email")
        }

        val existingUser = userRepository.findByEmail(emailAddress)
        if (existingUser != null) throw ValidationError("email already exists")

        val user = User(
            id = UserId(UUID.randomUUID()),
            name = name.trim(),
            email = emailAddress,
            password = password
        )

        userRepository.save(user)
        return user.id
    }
}