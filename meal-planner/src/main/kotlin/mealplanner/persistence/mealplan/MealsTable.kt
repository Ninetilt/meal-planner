package de.dhbw.mealplanner.persistence.mealplan


import de.dhbw.mealplanner.persistence.recipe.RecipesTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import org.jetbrains.exposed.v1.javatime.date

object MealsTable : UUIDTable("meals") {
    val mealPlanId = reference("meal_plan_id", MealPlansTable, onDelete = ReferenceOption.CASCADE)
    val date = date("date")
    val type = varchar("type", 50)
    val recipeId = optReference("recipe_id", RecipesTable)
}