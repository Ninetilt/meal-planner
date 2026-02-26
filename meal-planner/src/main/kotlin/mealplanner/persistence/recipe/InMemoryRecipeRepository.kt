package de.dhbw.mealplanner.persistence.recipe

import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository

class InMemoryRecipeRepository : RecipeRepository {
    private val storage = mutableMapOf<RecipeId, Recipe>()

    override fun save(recipe: Recipe) {
        storage[recipe.id] = recipe
    }

    override fun findById(id: RecipeId): Recipe? {
        return storage[id]
    }

    override fun findAll(): List<Recipe> {
        return storage.values.toList()
    }

    override fun delete(id: RecipeId) {
        storage.remove(id)
    }
}