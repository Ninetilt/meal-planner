package de.dhbw.mealplanner.api.dto.mealplan

import kotlinx.serialization.Serializable

@Serializable
data class AssignRecipeRequest(
    val recipeId: String
)