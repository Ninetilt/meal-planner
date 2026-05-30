package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.query.RecipeListItemView
import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    val id: String,
    val title: String
) {
    companion object {
        fun from(view: RecipeListItemView) = RecipeResponse(
            id = view.id,
            title = view.title
        )
    }
}
