package de.dhbw.mealplanner.application.recipe

import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.commands.CreateRecipeCommand
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import java.util.UUID

class CreateRecipeUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(title: String): RecipeId = execute(CreateRecipeCommand(title))

    fun execute(command: CreateRecipeCommand): RecipeId {
        if (command.title.isBlank()) throw ValidationError("title must not be blank")

        val recipe = Recipe(
            id = RecipeId(UUID.randomUUID()),
            title = command.title
        )
        recipeRepository.save(recipe)
        return recipe.id
    }
}
