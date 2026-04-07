package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.commands.ChangeIngredientQuantityCommand
import de.dhbw.mealplanner.domain.recipe.RecipeId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChangeIngredientQuantityRequest(
    val ingredient: String,
    val amount: Double,
    val unit: String
) {
    fun toCommand(recipeId: UUID) = ChangeIngredientQuantityCommand(
        recipeId = RecipeId(recipeId),
        ingredient = ingredient,
        amount = amount,
        unit = unit
    )
}
