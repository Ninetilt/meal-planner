package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeRequest(
    val title: String
)