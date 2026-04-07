package de.dhbw.mealplanner.application.mealplan.commands

import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealType
import java.time.LocalDate

data class CreateMealCommand(
    val mealPlanId: MealPlanId,
    val date: LocalDate,
    val type: MealType
)
