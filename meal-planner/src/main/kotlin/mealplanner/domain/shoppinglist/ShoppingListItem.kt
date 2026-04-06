package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientUnit

data class ShoppingListItem(
    val ingredient: IngredientName,
    val totalAmount: Double,
    val unit: IngredientUnit
)