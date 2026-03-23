package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class CreateMealPlanRequest(
    val name: String,
    val createdBy: String
)