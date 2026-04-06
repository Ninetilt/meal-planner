package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.DeleteMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.user.UserId
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class DeleteMealUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val useCase = DeleteMealUseCase(mealPlanRepository)

    @Test
    fun deleteMealAndSavePlan() {
        val planId = MealPlanId(UUID.randomUUID())
        val plan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 4, 4)), MealType.DINNER)

        every { mealPlanRepository.findById(planId) } returns plan
        every { mealPlanRepository.save(any()) } just Runs

        useCase.execute(planId, meal.id)

        assertEquals(0, plan.getMeals().size)
        verify(exactly = 1) { mealPlanRepository.save(plan) }
    }

    @Test
    fun throwNotFoundIfMealPlanNotFound() {
        val planId = MealPlanId(UUID.randomUUID())
        every { mealPlanRepository.findById(planId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(planId, MealId(UUID.randomUUID()))
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }

    @Test
    fun throwNotFoundIfMealNotFound() {
        val planId = MealPlanId(UUID.randomUUID())
        val plan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )

        every { mealPlanRepository.findById(planId) } returns plan

        assertThrows(NotFoundError::class.java) {
            useCase.execute(planId, MealId(UUID.randomUUID()))
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}
