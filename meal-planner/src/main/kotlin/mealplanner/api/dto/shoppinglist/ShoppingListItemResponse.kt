package de.dhbw.mealplanner.api.dto.shoppinglist

import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListItem
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItemResponse(
    val ingredient: String,
    val totalAmount: Double,
    val unit: String
) {
    companion object {
        fun from(item: ShoppingListItem) = ShoppingListItemResponse(
            ingredient = item.ingredient.value,
            totalAmount = item.totalAmount,
            unit = item.unit.code
        )
    }
}
