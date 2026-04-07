package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.common.ValidationError
import java.time.LocalDate
import java.util.UUID

fun parseUuidParam(value: String?, fieldName: String): UUID =
    runCatching { UUID.fromString(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

fun parseDateParam(value: String?, fieldName: String): LocalDate =
    runCatching { LocalDate.parse(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

inline fun <reified T : Enum<T>> parseEnumParam(value: String?, fieldName: String): T =
    runCatching { enumValueOf<T>(value.orEmpty()) }
        .getOrElse { throw ValidationError("invalid $fieldName") }
