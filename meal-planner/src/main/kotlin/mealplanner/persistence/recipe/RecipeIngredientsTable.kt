package de.dhbw.mealplanner.persistence.recipe

import org.jetbrains.exposed.v1.core.Table

object RecipeIngredientsTable : Table("recipe_ingredients") {
    val recipeId = reference("recipe_id", RecipesTable)
    val ingredient = varchar("ingredient", 255)
    val amount = double("amount")
    val unit = varchar("unit", 100)

    override val primaryKey = PrimaryKey(recipeId, ingredient)
}