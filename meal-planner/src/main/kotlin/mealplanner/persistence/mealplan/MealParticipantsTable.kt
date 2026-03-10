package de.dhbw.mealplanner.persistence.mealplan

import de.dhbw.mealplanner.persistence.user.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object MealParticipantsTable : Table("meal_participants") {
    val mealId = reference("meal_id", MealsTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(mealId, userId)
}