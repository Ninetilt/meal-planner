package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AddParticipantToMealUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = AddParticipantToMealUseCase(mealPlanRepository, userRepository)

    @Test
    fun addParticipantAndSavePlan() {
        val planId = MealPlanId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())

        val plan = MealPlan(planId)
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.LUNCH)

        every { mealPlanRepository.findById(planId) } returns plan
        every { userRepository.findById(userId) } returns User(userId, "Max")
        every { mealPlanRepository.save(any()) } just Runs

        useCase.execute(planId, meal.id, userId)

        assertTrue(meal.getParticipants().contains(userId))
        verify(exactly = 1) { mealPlanRepository.save(plan) }
    }

    @Test
    fun throwNotFoundIfUserNotFound() {
        val planId = MealPlanId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())

        val plan = MealPlan(planId)
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.DINNER)

        every { mealPlanRepository.findById(planId) } returns plan
        every { userRepository.findById(userId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(planId, meal.id, userId)
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}