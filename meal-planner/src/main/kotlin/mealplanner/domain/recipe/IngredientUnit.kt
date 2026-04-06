package de.dhbw.mealplanner.domain.recipe

enum class IngredientUnit(val code: String) {
    GRAM("g"),
    KILOGRAM("kg"),
    MILLILITER("ml"),
    LITER("l"),
    PIECE("piece"),
    TEASPOON("tsp"),
    TABLESPOON("tbsp"),
    CUP("cup");

    companion object {
        fun fromCode(code: String): IngredientUnit {
            val normalizedCode = code.trim().lowercase()
            return entries.firstOrNull { it.code == normalizedCode }
                ?: throw IllegalArgumentException("Unknown ingredient unit: $code")
        }
    }
}
