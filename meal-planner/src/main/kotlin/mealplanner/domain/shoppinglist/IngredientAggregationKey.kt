package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientUnit

data class IngredientAggregationKey(
    val ingredient: IngredientName,
    val unit: IngredientUnit
)