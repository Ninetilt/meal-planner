package de.dhbw.mealplanner.persistence.mealplan

import de.dhbw.mealplanner.persistence.user.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object MealPlansTable : UUIDTable("meal_plans") {
    val name = varchar("name", 255)
    val createdBy = reference("created_by", UsersTable, onDelete = ReferenceOption.RESTRICT)
}