package de.dhbw.mealplanner.api.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserResponse(
    val id: String
)
