package de.dhbw.mealplanner.application.mealplan.query

data class MealPlanView(
    val id: String,
    val mealCount: Int,
    val meals: List<MealView>
)