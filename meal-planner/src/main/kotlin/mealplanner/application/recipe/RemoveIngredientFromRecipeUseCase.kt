package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.commands.RemoveIngredientCommand
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class RemoveIngredientFromRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId, ingredient: String) = execute(
        RemoveIngredientCommand(
            recipeId = recipeId,
            ingredient = ingredient
        )
    )

    fun execute(command: RemoveIngredientCommand) {
        if (command.ingredient.isBlank()) throw ValidationError("ingredient must not be blank")

        val recipe = recipeRepository.findById(command.recipeId)
            ?: throw NotFoundError("recipe")

        recipe.removeIngredient(IngredientName(command.ingredient))

        recipeRepository.save(recipe)
    }
}
