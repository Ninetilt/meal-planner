package de.dhbw.mealplanner.application.mealplan.query

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class GetMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId): MealPlanView {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meals = plan.getMeals().map { meal ->
            MealView(
                id = meal.id.value.toString(),
                date = meal.date.value.toString(),
                type = meal.type.name,
                recipeId = meal.recipeId?.value?.toString(),
                participantCount = meal.getParticipants().size,
                responsibleCount = meal.getResponsibleUsers().size
            )
        }

        return MealPlanView(
            id = plan.id.value.toString(),
            mealCount = meals.size,
            meals = meals
        )
    }
}