package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailsResponse(
    val id: String,
    val title: String,
    val ingredients: List<IngredientResponse>,
    val description: String,
)