package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.commands.AddIngredientCommand
import de.dhbw.mealplanner.domain.recipe.RecipeId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddIngredientRequest(
    val ingredient: String,
    val amount: Double,
    val unit: String
) {
    fun toCommand(recipeId: UUID) = AddIngredientCommand(
        recipeId = RecipeId(recipeId),
        ingredient = ingredient,
        amount = amount,
        unit = unit
    )
}
