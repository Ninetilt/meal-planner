package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.query.RecipeView
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailsResponse(
    val id: String,
    val title: String,
    val ingredients: List<IngredientResponse>,
    val description: String,
) {
    companion object {
        fun from(view: RecipeView) = RecipeDetailsResponse(
            id = view.id,
            title = view.title,
            ingredients = view.ingredients.map(IngredientResponse::from),
            description = view.description
        )
    }
}
