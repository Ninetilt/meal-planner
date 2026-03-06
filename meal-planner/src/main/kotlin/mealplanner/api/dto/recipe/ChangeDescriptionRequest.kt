package de.dhbw.mealplanner.api.dto.recipe

import kotlinx.serialization.Serializable

@Serializable
data class ChangeDescriptionRequest(
    val description: String
)