package de.dhbw.mealplanner.persistence.recipe

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object RecipesTable : UUIDTable("recipes") {
    val title = varchar("title", 255)
    val description = text("description")
}