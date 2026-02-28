package de.dhbw.mealplanner.domain.shoppinglist

data class ShoppingList(
    val items: List<ShoppingListItem>,
    val recipesWithoutIngredients: List<String>
)