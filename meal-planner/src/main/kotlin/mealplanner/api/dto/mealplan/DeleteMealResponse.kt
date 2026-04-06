package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMealResponse(
    val id: String,
    val mealPlanId: String
)
