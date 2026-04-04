package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.recipe.IngredientName
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
        var mealsWithoutParticipants = 0

        val relevantMeals = mealPlan.getMeals()
            .filter { meal ->
                val date = meal.date.value
                !date.isBefore(startDate) && !date.isAfter(endDate)
            }

        for (meal in relevantMeals) {

            val recipeId = meal.recipeId ?: continue
            val recipe = recipesById[recipeId] ?: continue

            val portions = meal.portionCount()
            if (portions <= 0) {
                mealsWithoutParticipants++
                continue
            }

            val ingredients = recipe.getIngredients()

            if (ingredients.isEmpty()) {
                recipesWithoutIngredients.add(recipe.getTitle())
                continue
            }

            for (ingredientQuantity in ingredients) {

                val key = IngredientAggregationKey(
                    ingredientQuantity.ingredient,
                    ingredientQuantity.unit
                )

                val calculatedAmount =
                    ingredientQuantity.amount * portions.toDouble()

                aggregatedIngredients[key] =
                    aggregatedIngredients.getOrDefault(key, 0.0) + calculatedAmount
            }
        }

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
            mealsWithoutParticipants = mealsWithoutParticipants
        )
    }
}