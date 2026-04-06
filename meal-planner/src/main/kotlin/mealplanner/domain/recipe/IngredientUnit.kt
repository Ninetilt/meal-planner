package de.dhbw.mealplanner.domain.recipe

enum class IngredientUnit(
    val code: String,
    private val category: UnitCategory,
    private val factorToBaseUnit: Double
) {
    GRAM("g", category = UnitCategory.MASS, factorToBaseUnit = 1.0),
    KILOGRAM("kg", category = UnitCategory.MASS, factorToBaseUnit = 1000.0),
    MILLILITER("ml", category = UnitCategory.VOLUME, factorToBaseUnit = 1.0),
    LITER("l", category = UnitCategory.VOLUME, factorToBaseUnit = 1000.0),
    PIECE("piece", category = UnitCategory.COUNT, factorToBaseUnit = 1.0),
    TEASPOON("tsp", category = UnitCategory.VOLUME, factorToBaseUnit = 5.0),
    TABLESPOON("tbsp", category = UnitCategory.VOLUME, factorToBaseUnit = 15.0),
    CUP("cup", category = UnitCategory.VOLUME, factorToBaseUnit = 240.0);

    fun normalizeAmount(amount: Double): Double = amount * factorToBaseUnit

    fun normalizedUnit(): IngredientUnit = when (category) {
        UnitCategory.MASS -> GRAM
        UnitCategory.VOLUME -> MILLILITER
        UnitCategory.COUNT -> PIECE
    }

    companion object {
        fun fromCode(code: String): IngredientUnit {
            val normalizedCode = code.trim().lowercase()
            return entries.firstOrNull { it.code == normalizedCode }
                ?: throw IllegalArgumentException("Unknown ingredient unit: $code")
        }
    }

    private enum class UnitCategory {
        MASS,
        VOLUME,
        COUNT
    }
}
