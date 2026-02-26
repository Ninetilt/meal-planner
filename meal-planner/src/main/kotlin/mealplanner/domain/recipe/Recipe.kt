package de.dhbw.mealplanner.domain.recipe

class Recipe(
    val id: RecipeId,
    private var title: String,
    private val ingredients: MutableList<IngredientQuantity> = mutableListOf(),
    private val preparationSteps: MutableList<String> = mutableListOf()
) {

    fun changeTitle(newTitle: String) {
        require(newTitle.isNotBlank()) { "Titel darf nicht leer sein" }
        title = newTitle
    }

    fun addIngredient(ingredientQuantity: IngredientQuantity) {
        if (ingredients.any { it.ingredient == ingredientQuantity.ingredient }) {
            throw IllegalArgumentException("Ingredient already exists")
        }
        ingredients.add(ingredientQuantity)
    }

    fun removeIngredient(ingredient: IngredientName) {
        ingredients.removeIf { it.ingredient == ingredient }
    }

    fun addPreparationStep(step: String) {
        preparationSteps.add(step)
    }

    fun getIngredients(): List<IngredientQuantity> =
        ingredients.toList()

    fun getPreparationSteps(): List<String> =
        preparationSteps.toList()

    fun getTitle(): String = title
}