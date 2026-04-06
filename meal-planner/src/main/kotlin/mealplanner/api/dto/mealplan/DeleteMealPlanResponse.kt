package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMealPlanResponse(
    val id: String
)
