package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.recipe.IngredientName

data class IngredientAggregationKey(
    val ingredient: IngredientName,
    val unit: String
) {
    init {
        require(unit.isNotBlank()) { "Unit must not be blank" }
    }
}