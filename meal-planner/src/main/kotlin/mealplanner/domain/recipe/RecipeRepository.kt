package de.dhbw.mealplanner.domain.recipe

interface RecipeRepository {
    fun save(recipe: Recipe)
    fun findById(id: RecipeId): Recipe?
    fun findAll(): List<Recipe>
    fun delete(id: RecipeId)
}