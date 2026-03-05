package de.dhbw.mealplanner.domain.recipe

class Recipe(
    val id: RecipeId,
    private var title: String,
    private val ingredients: MutableList<IngredientQuantity> = mutableListOf(),
    private var description: String = ""
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

    fun changeDescription(newDescription: String) {
        description = newDescription
    }

    fun getIngredients(): List<IngredientQuantity> = ingredients.toList()

    fun getDescription(): String = description

    fun getTitle(): String = title
}