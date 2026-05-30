package de.dhbw.mealplanner.api.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class CreatedResourceResponse(
    val id: String
)