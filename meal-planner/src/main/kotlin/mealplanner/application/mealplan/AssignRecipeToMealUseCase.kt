package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class AssignRecipeToMealUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) {

    fun execute(
        mealPlanId: MealPlanId,
        mealId: MealId,
        recipeId: RecipeId
    ) {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan") as Throwable

        val meal = plan.getMeals().find { it.id == mealId }
            ?: throw NotFoundError("meal")

        val recipeExists = recipeRepository.findById(recipeId) != null
        if (!recipeExists) {
            throw NotFoundError("recipe")
        }

        meal.recipeId = recipeId

        mealPlanRepository.save(plan)
    }
}