package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class RemoveIngredientFromRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId, ingredient: String) {
        if (ingredient.isBlank()) throw ValidationError("ingredient must not be blank")

        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        recipe.removeIngredient(IngredientName(ingredient))

        recipeRepository.save(recipe)
    }
}