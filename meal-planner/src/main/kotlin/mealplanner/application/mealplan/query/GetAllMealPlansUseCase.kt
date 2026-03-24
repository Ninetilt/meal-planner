package de.dhbw.mealplanner.application.mealplan.query

import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class GetAllMealPlansUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(): List<MealPlanListItemView> {
        return mealPlanRepository.findAll()
            .map { mealPlan ->
                MealPlanListItemView(
                    id = mealPlan.id.value.toString(),
                    name = mealPlan.getName(),
                    createdBy = mealPlan.createdBy.value.toString(),
                    memberCount = mealPlan.getMembers().size,
                    mealCount = mealPlan.getMeals().size
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}