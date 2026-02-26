package de.dhbw.mealplanner.domain.recipe

data class IngredientQuantity(
    val ingredient: IngredientName,
    val amount: Double,
    val unit: String
) {
    init {
        require(amount > 0) { "Amount must be greater than zero" }
        require(unit.isNotBlank()) { "Unit must not be blank" }
    }
}