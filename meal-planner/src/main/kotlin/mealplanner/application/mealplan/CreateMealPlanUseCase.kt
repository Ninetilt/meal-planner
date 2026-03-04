package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import java.util.UUID

class CreateMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(): MealPlanId {
        val plan = MealPlan(MealPlanId(UUID.randomUUID()))
        mealPlanRepository.save(plan)
        return plan.id
    }
}