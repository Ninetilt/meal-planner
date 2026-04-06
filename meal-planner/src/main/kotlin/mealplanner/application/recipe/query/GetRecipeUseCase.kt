package de.dhbw.mealplanner.application.recipe.query

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class GetRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: RecipeId): RecipeView {
        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        return RecipeView(
            id = recipe.id.value.toString(),
            title = recipe.getTitle(),
            ingredients = recipe.getIngredients().map {
                IngredientView(
                    ingredient = it.ingredient.value,
                    amount = it.amount,
                    unit = it.unit.code
                )
            },
            description = recipe.getDescription()
        )
    }
}
