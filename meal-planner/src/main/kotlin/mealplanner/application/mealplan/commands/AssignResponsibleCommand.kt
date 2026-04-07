package de.dhbw.mealplanner.application.mealplan.commands

import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.user.UserId

data class AssignResponsibleCommand(
    val mealPlanId: MealPlanId,
    val mealId: MealId,
    val userId: UserId
)
