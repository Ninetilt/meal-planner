package de.dhbw.mealplanner.application.user

import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import java.util.UUID

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(name: String): UserId {
        if (name.isBlank()) throw ValidationError("name must not be blank")

        val user = User(
            id = UserId(UUID.randomUUID()),
            name = name
        )
        userRepository.save(user)
        return user.id
    }
}