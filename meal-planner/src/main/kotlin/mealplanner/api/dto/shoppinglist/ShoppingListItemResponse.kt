package de.dhbw.mealplanner.api.dto.shoppinglist

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemResponse(
    val ingredient: String,
    val totalAmount: Double,
    val unit: String
)