package de.dhbw.mealplanner.api.dto.shoppinglist


import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListResponse(
    val items: List<ShoppingListItemResponse>,
    val recipesWithoutIngredients: List<String>,
    val mealsWithoutParticipants: Int
)