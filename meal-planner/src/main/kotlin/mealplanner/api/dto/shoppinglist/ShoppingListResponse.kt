package de.dhbw.mealplanner.api.dto.shoppinglist

import de.dhbw.mealplanner.domain.shoppinglist.ShoppingList
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListResponse(
    val items: List<ShoppingListItemResponse>,
    val recipesWithoutIngredients: List<String>,
    val incompleteMeals: Int
) {
    companion object {
        fun from(shoppingList: ShoppingList) = ShoppingListResponse(
            items = shoppingList.items.map(ShoppingListItemResponse::from),
            recipesWithoutIngredients = shoppingList.recipesWithoutIngredients,
            incompleteMeals = shoppingList.incompleteMeals
        )
    }
}
