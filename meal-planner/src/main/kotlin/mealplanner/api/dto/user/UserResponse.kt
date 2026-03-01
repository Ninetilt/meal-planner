package de.dhbw.mealplanner.api.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val name: String
)