package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class CreateMealRequest(
    val date: String,
    val type: String
)