package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.query.MealView
import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val id: String,
    val date: String,
    val type: String,
    val recipeId: String?,
    val participantCount: Int,
    val responsibleCount: Int
) {
    companion object {
        fun from(view: MealView) = MealResponse(
            id = view.id,
            date = view.date,
            type = view.type,
            recipeId = view.recipeId,
            participantCount = view.participantCount,
            responsibleCount = view.responsibleCount
        )
    }
}
