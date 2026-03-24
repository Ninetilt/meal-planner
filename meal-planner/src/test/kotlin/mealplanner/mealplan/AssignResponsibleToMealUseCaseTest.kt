package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AssignResponsibleToMealUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = AssignResponsibleToMealUseCase(mealPlanRepository, userRepository)

    @Test
    fun assignResponsibleWhoIsParticipant() {
        val planId = MealPlanId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())

        val plan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.DINNER)
        meal.addParticipant(userId)

        every { mealPlanRepository.findById(planId) } returns plan
        every { userRepository.findById(userId) } returns User(
            userId, "Max",
            EmailAddress("max@mealplanner"),
            "123")
        every { mealPlanRepository.save(any()) } just Runs

        useCase.execute(planId, meal.id, userId)

        assertTrue(meal.getResponsibleUsers().contains(userId))
        verify(exactly = 1) { mealPlanRepository.save(plan) }
    }

    @Test
    fun throwExceptionIfAssignResponsibleUserIsNotParticipant() {
        val planId = MealPlanId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())

        val plan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.LUNCH)

        every { mealPlanRepository.findById(planId) } returns plan
        every { userRepository.findById(userId) } returns User(
            userId, "Max",
            EmailAddress("max@mealplanner"),
            "123")

        assertThrows(ValidationError::class.java) {
            useCase.execute(planId, meal.id, userId)
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}