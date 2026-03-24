package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.RemoveRecipeFromMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.user.UserId
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class RemoveRecipeFromMealUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val useCase = RemoveRecipeFromMealUseCase(mealPlanRepository)

    @Test
    fun removeRecipeFromMealAndSavePlan() {
        val planId = MealPlanId(UUID.randomUUID())

        val plan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )
        val meal = plan.createMeal(
            MealDate(LocalDate.of(2026, 2, 2)), MealType.LUNCH)
        meal.recipeId = RecipeId(UUID.randomUUID())

        every { mealPlanRepository.findById(planId) } returns plan
        every { mealPlanRepository.save(any()) } just Runs

        useCase.execute(planId, meal.id)

        assertNull(meal.recipeId)
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