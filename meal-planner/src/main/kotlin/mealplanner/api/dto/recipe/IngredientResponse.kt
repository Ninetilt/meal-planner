package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.query.IngredientView
import kotlinx.serialization.Serializable

@Serializable
data class IngredientResponse(
    val ingredient: String,
    val amount: Double,
    val unit: String
) {
    companion object {
        fun from(view: IngredientView) = IngredientResponse(
            ingredient = view.ingredient,
            amount = view.amount,
            unit = view.unit
        )
    }
}
