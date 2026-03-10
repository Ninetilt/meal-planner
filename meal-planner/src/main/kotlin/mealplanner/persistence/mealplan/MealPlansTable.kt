package de.dhbw.mealplanner.persistence.mealplan

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object MealPlansTable : UUIDTable("meal_plans")