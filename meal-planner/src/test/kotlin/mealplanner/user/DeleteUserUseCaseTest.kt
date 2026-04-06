package de.dhbw.mealplanner.user

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.user.DeleteUserUseCase
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val useCase = DeleteUserUseCase(userRepository)

    @Test
    fun deleteUser() {
        val userId = UserId(UUID.randomUUID())
        val user = User(
            id = userId,
            name = "Max",
            email = EmailAddress("max@example.com"),
            password = "secret123"
        )

        every { userRepository.findById(userId) } returns user
        every { userRepository.delete(userId) } just runs

        useCase.execute(userId)

        verify(exactly = 1) { userRepository.delete(userId) }
    }

    @Test
    fun throwNotFoundIfUserDoesNotExist() {
        val userId = UserId(UUID.randomUUID())
        every { userRepository.findById(userId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(userId)
        }

        verify(exactly = 0) { userRepository.delete(userId) }
    }
}
