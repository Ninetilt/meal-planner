package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.api.dto.recipe.AddIngredientCommand
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.IngredientUnit
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
    ) = execute(
        AddIngredientCommand(
            recipeId = recipeId,
            ingredient = ingredient,
            amount = amount,
            unit = unit
        )
    )

    fun execute(command: AddIngredientCommand) {
        if (command.ingredient.isBlank()) throw ValidationError("ingredient must not be blank")
        if (command.unit.isBlank()) throw ValidationError("unit must not be blank")
        if (command.amount <= 0.0) throw ValidationError("amount must be > 0")

        val ingredientUnit = try {
            IngredientUnit.fromCode(command.unit)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid unit")
        }

        val recipe = recipeRepository.findById(command.recipeId)
            ?: throw NotFoundError("recipe")

        val ingredientQuantity = IngredientQuantity(
            ingredient = IngredientName(command.ingredient),
            amount = command.amount,
            unit = ingredientUnit
        )

        try {
            recipe.addIngredient(ingredientQuantity)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "cannot add ingredient")
        }

        recipeRepository.save(recipe)
    }
}
