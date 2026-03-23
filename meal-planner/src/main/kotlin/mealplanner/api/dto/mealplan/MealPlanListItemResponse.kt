package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class MealPlanListItemResponse(
    val id: String,
    val name: String,
    val createdBy: String,
    val memberCount: Int,
    val mealCount: Int
)