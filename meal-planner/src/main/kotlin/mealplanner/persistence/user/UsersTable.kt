package de.dhbw.mealplanner.persistence.user

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object UsersTable : UUIDTable("users") {
    val name = varchar("name", 255)
}