package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class DeleteMealUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId) {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val mealExists = plan.getMeals().any { it.id == mealId }
        if (!mealExists) {
            throw NotFoundError("meal")
        }

        plan.removeMeal(mealId)

        mealPlanRepository.save(plan)
    }
}
