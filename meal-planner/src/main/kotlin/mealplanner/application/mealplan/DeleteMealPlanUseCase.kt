package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class DeleteMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId) {
        val mealPlan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        mealPlanRepository.delete(mealPlan.id)
    }
}
