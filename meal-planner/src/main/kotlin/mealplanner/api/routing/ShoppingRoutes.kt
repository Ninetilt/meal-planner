package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListItemResponse
import de.dhbw.mealplanner.api.dto.shoppinglist.ShoppingListResponse
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.shoppingRoutes(
    generateShoppingListUseCase: GenerateShoppingListUseCase
) {
    route("/shoppinglist") {
        get {
            val mealPlanIdParam = call.request.queryParameters["mealPlanId"]
            val startParam = call.request.queryParameters["start"]
            val endParam = call.request.queryParameters["end"]

            val planUuid = parseUuidParam(mealPlanIdParam, "mealPlanId")
            val start = parseDateParam(startParam, "start date")
            val end = parseDateParam(endParam, "end date")
            val shoppingList = generateShoppingListUseCase.execute(MealPlanId(planUuid), start, end)

            val response = ShoppingListResponse(
                items = shoppingList.items.map {
                    ShoppingListItemResponse(
                        ingredient = it.ingredient.value,
                        totalAmount = it.totalAmount,
                        unit = it.unit.code
                    )
                },
                recipesWithoutIngredients = shoppingList.recipesWithoutIngredients,
                incompleteMeals = shoppingList.incompleteMeals
            )

            call.respond(response)
        }
    }
}
