package de.dhbw.mealplanner.user

import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateUserUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val useCase = CreateUserUseCase(userRepository)

    @Test
    fun createUserWhenInputIsValid() {
        val savedUser = slot<User>()
        every { userRepository.findByEmail(EmailAddress("mario@example.com")) } returns null
        every { userRepository.save(capture(savedUser)) } just runs

        val userId = useCase.execute(
            name = " Mario ",
            email = " mario@example.com ",
            password = "secret123"
        )

        assertNotNull(userId)
        assertEquals("Mario", savedUser.captured.getName())
        assertEquals("mario@example.com", savedUser.captured.getEmail().value)
        assertEquals("secret123", savedUser.captured.getPassword())
        verify(exactly = 1) { userRepository.save(any()) }
    }

    @Test
    fun throwErrorWhenNameIsBlank() {
        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                name = "",
                email = "mario@example.com",
                password = "secret123"
            )
        }

        assertEquals("name must not be blank", exception.message)
        verify(exactly = 0) { userRepository.findByEmail(EmailAddress("mario@example.com")) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun throwErrorWhenPasswordIsBlank() {
        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                name = "mario",
                email = "mario@example.com",
                password = ""
            )
        }

        assertEquals("password must not be blank", exception.message)
        verify(exactly = 0) { userRepository.findByEmail(EmailAddress("mario@example.com")) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun throwErrorWhenEmailIsInvalid() {
        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                name = "mario",
                email = "invalid mail",
                password = "secret123"
            )
        }

        assertEquals("Email must contain @", exception.message)
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    fun throwErrorWhenEmailAlreadyExists() {
        val existingUser = User(
            id = UserId(UUID.randomUUID()),
            name = "Existing",
            email = EmailAddress("mario@example.com"),
            password = "secret123"
        )
        every { userRepository.findByEmail(EmailAddress("mario@example.com")) } returns existingUser

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                name = "mario",
                email = "mario@example.com",
                password = "secret123"
            )
        }

        assertEquals("email already exists", exception.message)
        verify(exactly = 0) { userRepository.save(any()) }
    }
}
