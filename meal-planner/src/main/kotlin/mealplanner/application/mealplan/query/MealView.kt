package de.dhbw.mealplanner.application.mealplan.query

data class MealView(
    val id: String,
    val date: String,
    val type: String,
    val recipeId: String?,
    val participantCount: Int,
    val responsibleCount: Int
)