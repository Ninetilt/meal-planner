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
            val planUuid = call.requireUuidQueryParam("mealPlanId")
            val start = call.requireDateQueryParam("start", "start date")
            val end = call.requireDateQueryParam("end", "end date")
            val shoppingList = generateShoppingListUseCase.execute(MealPlanId(planUuid), start, end)
            call.respond(ShoppingListResponse.from(shoppingList))
        }
    }
}
