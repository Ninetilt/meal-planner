package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.commands.CreateMealPlanCommand
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.user.UserId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateMealPlanRequest(
    val name: String,
    val createdBy: String
) {
    fun toCommand() = CreateMealPlanCommand(
        name = name,
        createdBy = UserId(parseUuid(createdBy, "createdBy"))
    )
}

private fun parseUuid(value: String, fieldName: String): UUID =
    runCatching { UUID.fromString(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }
