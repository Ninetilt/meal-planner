package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.AddUserToMealPlanUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
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

class AddUserToMealPlanUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = AddUserToMealPlanUseCase(mealPlanRepository, userRepository)

    @Test
    fun addUserToMealPlanAndSave() {
        val creatorId = UserId(UUID.randomUUID())
        val newUserId = UserId(UUID.randomUUID())
        val mealPlanId = MealPlanId(UUID.randomUUID())

        val mealPlan = MealPlan(
            id = mealPlanId,
            name = "WG Plan",
            createdBy = creatorId
        )

        val user = User(
            id = newUserId,
            name = "Anna",
            email = EmailAddress("anna@example.com"),
            password = "secret123"
        )

        every { mealPlanRepository.findById(mealPlanId) } returns mealPlan
        every { userRepository.findById(newUserId) } returns user
        every { mealPlanRepository.save(any()) } just runs

        useCase.execute(mealPlanId, newUserId)

        assertTrue(mealPlan.getMembers().contains(newUserId))
        verify(exactly = 1) { mealPlanRepository.save(mealPlan) }
    }
}