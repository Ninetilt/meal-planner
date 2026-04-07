package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.domain.recipe.RecipeId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChangeDescriptionRequest(
    val description: String
) {
    fun toCommand(recipeId: UUID) = ChangeDescriptionCommand(
        recipeId = RecipeId(recipeId),
        description = description
    )
}

data class ChangeDescriptionCommand(
    val recipeId: RecipeId,
    val description: String
)
