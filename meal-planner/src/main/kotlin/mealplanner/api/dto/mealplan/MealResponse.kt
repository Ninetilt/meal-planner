package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val id: String,
    val date: String,
    val type: String,
    val recipeId: String?,
    val participantCount: Int,
    val responsibleCount: Int
)