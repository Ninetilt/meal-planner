package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListItemResponse
import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListResponse
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.util.UUID

fun Route.shoppingRoutes(
    mealPlanRepository: MealPlanRepository,
    shoppingListGenerator: ShoppingListGenerator
) {
    route("/shoppinglist") {
        get {
            val mealPlanIdParam = call.request.queryParameters["mealPlanId"]
            val startParam = call.request.queryParameters["start"]
            val endParam = call.request.queryParameters["end"]

            val planUuid = runCatching { UUID.fromString(mealPlanIdParam) }.getOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid mealPlanId")

            val start = runCatching { LocalDate.parse(startParam) }.getOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid start date")

            val end = runCatching { LocalDate.parse(endParam) }.getOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid end date")

            val plan = mealPlanRepository.findById(MealPlanId(planUuid))
                ?: return@get call.respond(HttpStatusCode.NotFound, "mealplan not found")

            val list = shoppingListGenerator.generate(plan, start, end)

            val response = ShoppingListResponse(
                items = list.items.map {
                    ShoppingListItemResponse(
                        ingredient = it.ingredient.value,
                        totalAmount = it.totalAmount,
                        unit = it.unit
                    )
                },
                recipesWithoutIngredients = list.recipesWithoutIngredients,
                mealsWithoutParticipants = list.mealsWithoutParticipants
            )

            call.respond(response)
        }
    }
}