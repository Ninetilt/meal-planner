package de.dhbw.mealplanner.application.recipe.commands

import de.dhbw.mealplanner.domain.recipe.RecipeId

data class ChangeDescriptionCommand(
    val recipeId: RecipeId,
    val description: String
)
