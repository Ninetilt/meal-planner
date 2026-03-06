package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class RemoveRecipeFromMealUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId) {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.getMeals().find { it.id == mealId }
            ?: throw NotFoundError("meal")

        meal.recipeId = null

        mealPlanRepository.save(plan)
    }
}