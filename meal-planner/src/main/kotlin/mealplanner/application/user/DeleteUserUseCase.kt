package de.dhbw.mealplanner.application.user

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(userId: UserId) {
        val user = userRepository.findById(userId)
            ?: throw NotFoundError("user")

        userRepository.delete(user.id)
    }
}
