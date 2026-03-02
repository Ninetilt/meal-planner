package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

// Erstmal nur für debugging gedacht
@Serializable
data class MealDebugResponse(
    val id: String,
    val date: String,
    val type: String,
    val recipeId: String?,
    val participantCount: Int,
    val responsibleCount: Int
)

@Serializable
data class MealPlanDebugResponse(
    val id: String,
    val mealCount: Int,
    val meals: List<MealDebugResponse>
)