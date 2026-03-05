package de.dhbw.mealplanner.application.recipe.query

import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class GetAllRecipesUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(): List<RecipeListItemView> {
        return recipeRepository.findAll()
            .map { recipe ->
                RecipeListItemView(
                    id = recipe.id.value.toString(),
                    title = recipe.getTitle()
                )
            }
            .sortedBy { it.title.lowercase() }
    }
}