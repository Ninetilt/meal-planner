package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class ChangeRecipeDescriptionUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId, description: String) {
        val normalized = description

        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        recipe.changeDescription(normalized)

        recipeRepository.save(recipe)
    }
}