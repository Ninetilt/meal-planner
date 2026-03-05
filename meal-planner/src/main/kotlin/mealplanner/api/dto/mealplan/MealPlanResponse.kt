package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class MealPlanResponse(
    val id: String,
    val mealCount: Int,
    val meals: List<MealResponse>
)