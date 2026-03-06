package de.dhbw.mealplanner.application.common

import kotlinx.serialization.Serializable

@Serializable
data class IdResponse(
    val id: String
)