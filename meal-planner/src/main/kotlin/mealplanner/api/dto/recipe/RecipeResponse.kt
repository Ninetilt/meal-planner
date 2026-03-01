package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val id: String,
    val title: String
)