package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.query.MealPlanListItemView
import kotlinx.serialization.Serializable

@Serializable
data class MealPlanListItemResponse(
    val id: String,
    val name: String,
    val createdBy: String,
    val memberCount: Int,
    val mealCount: Int
) {
    companion object {
        fun from(view: MealPlanListItemView) = MealPlanListItemResponse(
            id = view.id,
            name = view.name,
            createdBy = view.createdBy,
            memberCount = view.memberCount,
            mealCount = view.mealCount
        )
    }
}
