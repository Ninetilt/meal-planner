package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.common.ValidationError
import io.ktor.server.application.ApplicationCall
import java.time.LocalDate
import java.util.UUID

fun parseUuid(value: String?, fieldName: String): UUID =
    runCatching { UUID.fromString(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

fun parseDate(value: String?, fieldName: String): LocalDate =
    runCatching { LocalDate.parse(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

inline fun <reified T : Enum<T>> parseEnum(value: String?, fieldName: String): T =
    runCatching { enumValueOf<T>(value.orEmpty()) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

fun ApplicationCall.requireUuidParam(name: String): UUID =
    parseUuid(parameters[name], name)

fun ApplicationCall.requireUuidQueryParam(name: String): UUID =
    parseUuid(request.queryParameters[name], name)

fun ApplicationCall.requireDateQueryParam(name: String, fieldName: String = name): LocalDate =
    parseDate(request.queryParameters[name], fieldName)
