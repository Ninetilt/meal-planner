package de.dhbw.mealplanner.persistence.mealplan

import de.dhbw.mealplanner.persistence.user.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object MealPlanMembersTable : Table("meal_plan_members") {
    val mealPlanId = reference("meal_plan_id", MealPlansTable, onDelete = ReferenceOption.CASCADE)
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(mealPlanId, userId)
}