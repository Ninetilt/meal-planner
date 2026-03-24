package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.mealplan.CreateMealPlanUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateMealPlanUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = CreateMealPlanUseCase(mealPlanRepository, userRepository)

    @Test
    fun createMealPlanWhenInputIsValid() {
        val userId = UserId(UUID.randomUUID())
        val user = User(
            id = userId,
            name = "Max",
            email = EmailAddress("max@example.com"),
            password = "secret123"
        )

        every { userRepository.findById(userId) } returns user
        every { mealPlanRepository.save(any()) } just runs

        val mealPlanId = useCase.execute(
            name = "WG Plan",
            createdBy = userId
        )

        assertNotNull(mealPlanId)
        verify(exactly = 1) { mealPlanRepository.save(any()) }
    }

    @Test
    fun throwErrorWhenNameIsBlank() {
        val userId = UserId(UUID.randomUUID())

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                name = "",
                createdBy = userId
            )
        }

        assertEquals("meal plan name must not be blank", exception.message)
        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }

    @Test
    fun throwNotFoundWhenCreatorDoesNotExist() {
        val userId = UserId(UUID.randomUUID())

        every { userRepository.findById(userId) } returns null

        val exception = assertThrows(NotFoundError::class.java) {
            useCase.execute(
                name = "WG Plan",
                createdBy = userId
            )
        }

        assertEquals("user not found", exception.message)
        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}