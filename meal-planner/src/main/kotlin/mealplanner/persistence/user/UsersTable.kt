package de.dhbw.mealplanner.persistence.user

import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable

object UsersTable : UUIDTable("users") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
}