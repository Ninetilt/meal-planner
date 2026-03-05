package de.dhbw.mealplanner.application.mealplan.query

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class GetMealUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId): MealView {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.getMeals().find { it.id == mealId }
            ?: throw NotFoundError("meal")

        return MealView(
            id = meal.id.value.toString(),
            date = meal.date.value.toString(),
            type = meal.type.name,
            recipeId = meal.recipeId?.value?.toString(),
            participantCount = meal.getParticipants().size,
            responsibleCount = meal.getResponsibleUsers().size
        )
    }
}