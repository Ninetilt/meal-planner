package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RemoveIngredientRequest(
    val ingredient: String
)
