package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class DeleteRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId) {
        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        recipeRepository.delete(recipe.id)
    }
}
