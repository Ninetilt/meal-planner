package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.commands.CreateMealCommand
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealType
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class CreateMealRequest(
    val date: String,
    val type: String
) {
    fun toCommand(planId: UUID) = CreateMealCommand(
        mealPlanId = MealPlanId(planId),
        date = parseDate(date, "date"),
        type = parseMealType(type, "type")
    )
}

private fun parseDate(value: String, fieldName: String): LocalDate =
    runCatching { LocalDate.parse(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }

private fun parseMealType(value: String, fieldName: String): MealType =
    runCatching { enumValueOf<MealType>(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }
