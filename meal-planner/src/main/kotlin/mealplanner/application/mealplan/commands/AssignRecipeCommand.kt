package de.dhbw.mealplanner.application.mealplan.commands

import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.recipe.RecipeId

data class AssignRecipeCommand(
    val mealPlanId: MealPlanId,
    val mealId: MealId,
    val recipeId: RecipeId
)
