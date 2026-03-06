package de.dhbw.mealplanner.persistence.recipe

import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class SqlRecipeRepository : RecipeRepository {

    override fun save(recipe: Recipe) {
        transaction {
            val existing = RecipesTable.selectAll()
                .where { RecipesTable.id eq recipe.id.value }
                .singleOrNull()

            if (existing == null) {
                RecipesTable.insert {
                    it[id] = recipe.id.value
                    it[title] = recipe.getTitle()
                    it[description] = recipe.getDescription()
                }
            } else {
                RecipesTable.update({ RecipesTable.id eq recipe.id.value }) {
                    it[title] = recipe.getTitle()
                    it[description] = recipe.getDescription()
                }

                RecipeIngredientsTable.deleteWhere {
                    RecipeIngredientsTable.recipeId eq recipe.id.value
                }
            }

            if (existing == null) {
                RecipeIngredientsTable.deleteWhere {
                    RecipeIngredientsTable.recipeId eq recipe.id.value
                }
            }

            recipe.getIngredients().forEach { ingredientQuantity ->
                RecipeIngredientsTable.insert {
                    it[recipeId] = recipe.id.value
                    it[ingredient] = ingredientQuantity.ingredient.value
                    it[amount] = ingredientQuantity.amount
                    it[unit] = ingredientQuantity.unit
                }
            }
        }
    }

    override fun findById(id: RecipeId): Recipe? {
        return transaction {
            val recipeRow = RecipesTable.selectAll()
                .where { RecipesTable.id eq id.value }
                .singleOrNull()
                ?: return@transaction null

            val ingredients = RecipeIngredientsTable.selectAll()
                .where { RecipeIngredientsTable.recipeId eq id.value }
                .map {
                    IngredientQuantity(
                        ingredient = IngredientName(it[RecipeIngredientsTable.ingredient]),
                        amount = it[RecipeIngredientsTable.amount],
                        unit = it[RecipeIngredientsTable.unit]
                    )
                }
                .toMutableList()

            Recipe(
                id = RecipeId(recipeRow[RecipesTable.id].value),
                title = recipeRow[RecipesTable.title],
                description = recipeRow[RecipesTable.description],
                ingredients = ingredients
            )
        }
    }

    override fun findAll(): List<Recipe> {
        return transaction {
            RecipesTable.selectAll().map { recipeRow ->
                val recipeId = RecipeId(recipeRow[RecipesTable.id].value)

                val ingredients = RecipeIngredientsTable.selectAll()
                    .where { RecipeIngredientsTable.recipeId eq recipeId.value }
                    .map {
                        IngredientQuantity(
                            ingredient = IngredientName(it[RecipeIngredientsTable.ingredient]),
                            amount = it[RecipeIngredientsTable.amount],
                            unit = it[RecipeIngredientsTable.unit]
                        )
                    }
                    .toMutableList()

                Recipe(
                    id = recipeId,
                    title = recipeRow[RecipesTable.title],
                    description = recipeRow[RecipesTable.description],
                    ingredients = ingredients
                )
            }
        }
    }

    override fun delete(id: RecipeId) {
        transaction {
            RecipeIngredientsTable.deleteWhere {
                RecipeIngredientsTable.recipeId eq id.value
            }

            RecipesTable.deleteWhere {
                RecipesTable.id eq id.value
            }
        }
    }
}