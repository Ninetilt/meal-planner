package de.dhbw.mealplanner.application.recipe.commands

import de.dhbw.mealplanner.domain.recipe.RecipeId

data class RemoveIngredientCommand(
    val recipeId: RecipeId,
    val ingredient: String
)
