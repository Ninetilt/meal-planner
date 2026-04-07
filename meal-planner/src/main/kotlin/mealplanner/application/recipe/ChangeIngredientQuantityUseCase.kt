package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.api.dto.recipe.ChangeIngredientQuantityCommand
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
    ) = execute(
        ChangeIngredientQuantityCommand(
            recipeId = recipeId,
            ingredient = ingredient,
            amount = amount,
            unit = unit
        )
    )

    fun execute(command: ChangeIngredientQuantityCommand) {
        if (command.ingredient.isBlank()) {
            throw ValidationError("ingredient must not be blank")
        }
        if (command.amount <= 0.0) {
            throw ValidationError("amount must be > 0")
        }
        if (command.unit.isBlank()) {
            throw ValidationError("unit must not be blank")
        }

        val ingredientUnit = try {
            IngredientUnit.fromCode(command.unit)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid unit")
        }

        val recipe = recipeRepository.findById(command.recipeId)
            ?: throw NotFoundError("recipe")

        try {
            recipe.changeIngredientQuantity(
                ingredient = IngredientName(command.ingredient),
                newAmount = command.amount,
                newUnit = ingredientUnit
            )
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "cannot change ingredient quantity")
        }

        recipeRepository.save(recipe)
    }
}
