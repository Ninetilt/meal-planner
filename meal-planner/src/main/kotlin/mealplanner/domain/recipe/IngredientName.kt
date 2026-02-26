package de.dhbw.mealplanner.domain.recipe

@JvmInline
value class IngredientName(val value: String) {
    init {
        require(value.isNotBlank()) { "Ingredient name must not be blank" }
    }
}