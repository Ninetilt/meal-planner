package de.dhbw.mealplanner.application.mealplan.commands

import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.user.UserId

data class AddUserToMealPlanCommand(
    val mealPlanId: MealPlanId,
    val userId: UserId
)
