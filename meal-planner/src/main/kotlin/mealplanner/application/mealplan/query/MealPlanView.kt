package de.dhbw.mealplanner.application.mealplan.query

data class MealPlanView(
    val id: String,
    val name: String,
    val createdBy: String,
    val memberCount: Int,
    val mealCount: Int,
    val meals: List<MealView>
)