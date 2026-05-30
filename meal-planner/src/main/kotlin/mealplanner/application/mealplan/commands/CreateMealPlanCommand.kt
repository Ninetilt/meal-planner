package de.dhbw.mealplanner.application.mealplan.commands

import de.dhbw.mealplanner.domain.user.UserId

data class CreateMealPlanCommand(
    val name: String,
    val createdBy: UserId
)
