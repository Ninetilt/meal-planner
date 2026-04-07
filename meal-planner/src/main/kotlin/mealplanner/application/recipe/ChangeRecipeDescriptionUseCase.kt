package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.recipe.commands.ChangeDescriptionCommand
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class ChangeRecipeDescriptionUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId, description: String) = execute(
        ChangeDescriptionCommand(
            recipeId = recipeId,
            description = description
        )
    )

    fun execute(command: ChangeDescriptionCommand) {
        val normalized = command.description

        val recipe = recipeRepository.findById(command.recipeId)
            ?: throw NotFoundError("recipe")

        recipe.changeDescription(normalized)

        recipeRepository.save(recipe)
    }
}
