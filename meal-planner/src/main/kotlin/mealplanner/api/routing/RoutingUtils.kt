package de.dhbw.mealplanner.api.routing

import java.util.UUID

fun parseUuidParam(value: String?): UUID? =
    runCatching { UUID.fromString(value) }.getOrNull()