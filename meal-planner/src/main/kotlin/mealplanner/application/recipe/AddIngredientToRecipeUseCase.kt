package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class AddIngredientToRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(
        recipeId: RecipeId,
        ingredient: String,
        amount: Double,
        unit: String
    ) {
        if (ingredient.isBlank()) throw ValidationError("ingredient must not be blank")
        if (unit.isBlank()) throw ValidationError("unit must not be blank")
        if (amount <= 0.0) throw ValidationError("amount must be > 0")

        val recipe = recipeRepository.findById(recipeId)
            ?: throw NotFoundError("recipe")

        val ingredientQuantity = IngredientQuantity(
            ingredient = IngredientName(ingredient),
            amount = amount,
            unit = unit
        )

        try {
            recipe.addIngredient(ingredientQuantity)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "cannot add ingredient")
        }

        recipeRepository.save(recipe)
    }
}