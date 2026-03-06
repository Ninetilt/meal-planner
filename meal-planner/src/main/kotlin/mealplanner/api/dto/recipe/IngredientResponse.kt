package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class IngredientResponse(
    val ingredient: String,
    val amount: Double,
    val unit: String
)