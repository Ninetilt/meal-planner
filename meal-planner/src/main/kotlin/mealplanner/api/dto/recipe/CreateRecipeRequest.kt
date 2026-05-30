package de.dhbw.mealplanner.api.dto.recipe

import de.dhbw.mealplanner.application.recipe.commands.CreateRecipeCommand
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeRequest(
    val title: String
) {
    fun toCommand() = CreateRecipeCommand(title = title)
}
