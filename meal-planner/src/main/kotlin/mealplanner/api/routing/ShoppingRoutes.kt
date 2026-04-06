package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListItemResponse
import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListResponse
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.util.UUID

fun Route.shoppingRoutes(
    generateShoppingListUseCase: GenerateShoppingListUseCase
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

            val shoppingList = try {
                generateShoppingListUseCase.execute(MealPlanId(planUuid), start, end)
            } catch (e: ValidationError) {
                return@get call.respond(HttpStatusCode.BadRequest, e.message ?: "validation error")
            } catch (e: NotFoundError) {
                return@get call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            val response = ShoppingListResponse(
                items = shoppingList.items.map {
                    ShoppingListItemResponse(
                        ingredient = it.ingredient.value,
                        totalAmount = it.totalAmount,
                        unit = it.unit
                    )
                },
                recipesWithoutIngredients = shoppingList.recipesWithoutIngredients,
                incompleteMeals = shoppingList.incompleteMeals
            )

            call.respond(response)
        }
    }
}