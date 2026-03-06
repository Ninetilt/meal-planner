package de.dhbw.mealplanner.application.recipe.query

data class RecipeView(
    val id: String,
    val title: String,
    val ingredients: List<IngredientView>,
    val description: String
)