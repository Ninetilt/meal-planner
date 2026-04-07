package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.query.MealPlanView
import kotlinx.serialization.Serializable

@Serializable
data class MealPlanResponse(
    val id: String,
    val name: String,
    val createdBy: String,
    val memberCount: Int,
    val mealCount: Int,
    val meals: List<MealResponse>
) {
    companion object {
        fun from(view: MealPlanView) = MealPlanResponse(
            id = view.id,
            name = view.name,
            createdBy = view.createdBy,
            memberCount = view.memberCount,
            mealCount = view.mealCount,
            meals = view.meals.map(MealResponse::from)
        )
    }
}
