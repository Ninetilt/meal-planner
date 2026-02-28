package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.recipe.IngredientName

data class ShoppingListItem(
    val ingredient: IngredientName,
    val totalAmount: Double,
    val unit: String
)