package de.dhbw.mealplanner.application.recipe.commands

import de.dhbw.mealplanner.domain.recipe.RecipeId

data class ChangeIngredientQuantityCommand(
    val recipeId: RecipeId,
    val ingredient: String,
    val amount: Double,
    val unit: String
)
