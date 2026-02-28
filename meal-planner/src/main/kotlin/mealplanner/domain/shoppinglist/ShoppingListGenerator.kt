package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import java.time.LocalDate

class ShoppingListGenerator(
    private val recipeRepository: RecipeRepository
) {

    fun generate(
        mealPlan: MealPlan,
        startDate: LocalDate,
        endDate: LocalDate
    ): ShoppingList {

        val aggregatedIngredients = mutableMapOf<Pair<String, String>, Double>()
        val recipesWithoutIngredients = mutableListOf<String>()

        val relevantMeals = mealPlan.getMeals()
            .filter { meal ->
                val date = meal.date.value
                !date.isBefore(startDate) && !date.isAfter(endDate)
            }

        for (meal in relevantMeals) {

            val recipeId = meal.recipeId ?: continue
            val recipe = recipeRepository.findById(recipeId) ?: continue

            val ingredients = recipe.getIngredients()

            if (ingredients.isEmpty()) {
                recipesWithoutIngredients.add(recipe.getTitle())
                continue
            }

            for (ingredientQuantity in ingredients) {

                val key = Pair(
                    ingredientQuantity.ingredient.value,
                    ingredientQuantity.unit
                )

                aggregatedIngredients[key] =
                    aggregatedIngredients.getOrDefault(key, 0.0) +
                            ingredientQuantity.amount
            }
        }

        val items = aggregatedIngredients.map { (key, totalAmount) ->
            ShoppingListItem(
                ingredient = IngredientName(key.first),
                totalAmount = totalAmount,
                unit = key.second
            )
        }

        return ShoppingList(
            items = items,
            recipesWithoutIngredients = recipesWithoutIngredients
        )
    }
}