package de.dhbw.mealplanner.domain.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import java.time.LocalDate

interface ShoppingListCalculationStrategy {

    fun calculate(
        mealPlan: MealPlan,
        recipesById: Map<RecipeId, Recipe>,
        startDate: LocalDate,
        endDate: LocalDate
    ): ShoppingList
}