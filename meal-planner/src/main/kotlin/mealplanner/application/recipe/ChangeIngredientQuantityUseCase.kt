package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientUnit
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class ChangeIngredientQuantityUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(
        recipeId: RecipeId,
        ingredient: String,
        amount: Double,
        unit: String
    ) {
        if (ingredient.isBlank()) {
            throw ValidationError("ingredient must not be blank")
        }
        if (amount <= 0.0) {
            throw ValidationError("amount must be > 0")
        }
        if (unit.isBlank()) {
            throw ValidationError("unit must not be blank")
        }

        val ingredientUnit = try {
            IngredientUnit.fromCode(unit)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid unit")
        }

        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        try {
            recipe.changeIngredientQuantity(
                ingredient = IngredientName(ingredient),
                newAmount = amount,
                newUnit = ingredientUnit
            )
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "cannot change ingredient quantity")
        }

        recipeRepository.save(recipe)
    }
}
