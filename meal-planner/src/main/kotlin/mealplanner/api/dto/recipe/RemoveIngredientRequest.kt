package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.domain.recipe.RecipeId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RemoveIngredientRequest(
    val ingredient: String
) {
    fun toCommand(recipeId: UUID) = RemoveIngredientCommand(
        recipeId = RecipeId(recipeId),
        ingredient = ingredient
    )
}

data class RemoveIngredientCommand(
    val recipeId: RecipeId,
    val ingredient: String
)
