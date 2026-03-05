package de.dhbw.mealplanner.application.recipe.query

data class IngredientView(
    val ingredient: String,
    val amount: Double,
    val unit: String
)