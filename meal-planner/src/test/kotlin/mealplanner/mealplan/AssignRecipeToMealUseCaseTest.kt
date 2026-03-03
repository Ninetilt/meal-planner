package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class AssignRecipeToMealUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val recipeRepository = mockk<RecipeRepository>()
    private val useCase = AssignRecipeToMealUseCase(mealPlanRepository, recipeRepository)

    @Test
    fun assignRecipeToMealAndSavePlan() {
        val planId = MealPlanId(UUID.randomUUID())
        val recipeId = RecipeId(UUID.randomUUID())

        val plan = MealPlan(planId)
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.LUNCH)

        every { mealPlanRepository.findById(planId) } returns plan
        every { recipeRepository.findById(recipeId) } returns Recipe(recipeId, "Nudeln mit Pesto")
        every { mealPlanRepository.save(any()) } just Runs

        useCase.execute(planId, meal.id, recipeId)

        assertEquals(recipeId, meal.recipeId)
        verify(exactly = 1) { mealPlanRepository.save(plan) }
    }

    @Test
    fun `throws NotFoundError when mealplan does not exist`() {
        val planId = MealPlanId(UUID.randomUUID())
        every { mealPlanRepository.findById(planId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(
                planId,
                MealId(UUID.randomUUID()),
                RecipeId(UUID.randomUUID())
            )
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }

    @Test
    fun throwNotFoundIfMealNotFound() {
        val planId = MealPlanId(UUID.randomUUID())
        val plan = MealPlan(planId)

        every { mealPlanRepository.findById(planId) } returns plan

        assertThrows(NotFoundError::class.java) {
            useCase.execute(
                planId,
                MealId(UUID.randomUUID()),
                RecipeId(UUID.randomUUID())
            )
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }

    @Test
    fun throwNotFoundIfRecipeNotFound() {
        val planId = MealPlanId(UUID.randomUUID())
        val recipeId = RecipeId(UUID.randomUUID())

        val plan = MealPlan(planId)
        val meal = plan.createMeal(MealDate(LocalDate.of(2026, 3, 3)),
            MealType.DINNER)

        every { mealPlanRepository.findById(planId) } returns plan
        every { recipeRepository.findById(recipeId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(planId, meal.id, recipeId)
        }

        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}