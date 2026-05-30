package de.dhbw.mealplanner.api.dto.mealplan

import de.dhbw.mealplanner.application.mealplan.commands.AddUserToMealPlanCommand
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.user.UserId
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddUserToMealPlanRequest(
    val userId: String
) {
    fun toCommand(planId: UUID) = AddUserToMealPlanCommand(
        mealPlanId = MealPlanId(planId),
        userId = UserId(parseUuid(userId, "userId"))
    )
}

private fun parseUuid(value: String, fieldName: String): UUID =
    runCatching { UUID.fromString(value) }
        .getOrElse { throw ValidationError("invalid $fieldName") }
