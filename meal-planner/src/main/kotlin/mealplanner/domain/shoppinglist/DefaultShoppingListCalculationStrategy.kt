package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.Meal
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import java.time.LocalDate

class DefaultShoppingListCalculationStrategy : ShoppingListCalculationStrategy {

    override fun calculate(
        mealPlan: MealPlan,
        recipesById: Map<RecipeId, Recipe>,
        startDate: LocalDate,
        endDate: LocalDate
    ): ShoppingList {

        val aggregatedIngredients = mutableMapOf<IngredientAggregationKey, Double>()
        val recipesWithoutIngredients = mutableListOf<String>()
        var incompleteMeals = 0

        val relevantMeals = findRelevantMeals(mealPlan, startDate, endDate)

        for (meal in relevantMeals) {
            val isComplete = processMeal(
                meal,
                recipesById,
                aggregatedIngredients,
                recipesWithoutIngredients
            )

            if (!isComplete) {
                incompleteMeals++
            }
        }

        return createShoppingList(
            aggregatedIngredients,
            recipesWithoutIngredients,
            incompleteMeals
        )
    }

    private fun findRelevantMeals(
        mealPlan: MealPlan,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Meal> {
        return mealPlan.getMeals()
            .filter { meal ->
                val date = meal.date.value
                !date.isBefore(startDate) && !date.isAfter(endDate)
            }
    }

    private fun processMeal(
        meal: Meal,
        recipesById: Map<RecipeId, Recipe>,
        aggregatedIngredients: MutableMap<IngredientAggregationKey, Double>,
        recipesWithoutIngredients: MutableList<String>
    ): Boolean {

        val recipeId = meal.recipeId ?: return false
        val recipe = recipesById[recipeId] ?: return false

        val portions = meal.portionCount()
        if (portions <= 0) {
            return false
        }

        val ingredients = recipe.getIngredients()
        if (ingredients.isEmpty()) {
            recipesWithoutIngredients.add(recipe.getTitle())
            return true
        }

        aggregateIngredients(
            ingredients,
            portions,
            aggregatedIngredients
        )

        return true
    }

    private fun aggregateIngredients(
        ingredients: List<IngredientQuantity>,
        portions: Int,
        aggregatedIngredients: MutableMap<IngredientAggregationKey, Double>
    ) {
        for (ingredientQuantity in ingredients) {
            val normalizedUnit = ingredientQuantity.unit.normalizedUnit()

            val key = IngredientAggregationKey(
                ingredient = ingredientQuantity.ingredient,
                unit = normalizedUnit
            )

            val calculatedAmount = ingredientQuantity.unit
                .normalizeAmount(ingredientQuantity.amount) * portions.toDouble()

            aggregatedIngredients[key] =
                aggregatedIngredients.getOrDefault(key, 0.0) + calculatedAmount
        }
    }

    private fun createShoppingList(
        aggregatedIngredients: Map<IngredientAggregationKey, Double>,
        recipesWithoutIngredients: List<String>,
        incompleteMeals: Int
    ): ShoppingList {

        val items = aggregatedIngredients.map { (key, totalAmount) ->
            ShoppingListItem(
                ingredient = key.ingredient,
                totalAmount = totalAmount,
                unit = key.unit
            )
        }

        return ShoppingList(
            items = items,
            recipesWithoutIngredients = recipesWithoutIngredients,
            incompleteMeals = incompleteMeals
        )
    }
}