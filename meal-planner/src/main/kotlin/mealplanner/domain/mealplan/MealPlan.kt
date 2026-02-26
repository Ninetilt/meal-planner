package de.dhbw.mealplanner.domain.mealplan

import java.util.UUID

class MealPlan(
    val id: MealPlanId,
    private val meals: MutableList<Meal> = mutableListOf()
) {

    fun createMeal(date: MealDate, type: MealType): Meal {

        if (meals.any { it.date == date && it.type == type }) {
            throw IllegalArgumentException("Meal for this date and type already exists")
        }

        val meal = Meal(
            id = MealId(UUID.randomUUID()),
            date = date,
            type = type,
            recipeId = null
        )

        meals.add(meal)
        return meal
    }

    fun removeMeal(mealId: MealId) {
        meals.removeIf { it.id == mealId }
    }

    fun getMeals(): List<Meal> = meals.toList()

}