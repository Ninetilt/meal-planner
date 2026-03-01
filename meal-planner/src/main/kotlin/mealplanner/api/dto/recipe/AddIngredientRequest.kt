package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class AddIngredientRequest(
    val ingredient: String,
    val amount: Double,
    val unit: String
)