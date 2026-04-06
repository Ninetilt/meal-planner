package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class DeleteRecipeResponse(
    val id: String
)
