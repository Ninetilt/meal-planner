package de.dhbw.mealplanner.application.mealplan.query

data class MealPlanListItemView(
    val id: String,
    val name: String,
    val createdBy: String,
    val memberCount: Int,
    val mealCount: Int
)