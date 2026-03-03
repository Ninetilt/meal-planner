package de.dhbw.mealplanner

import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.api.routing.registerRoutes
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import de.dhbw.mealplanner.persistence.mealplan.InMemoryMealPlanRepository
import de.dhbw.mealplanner.persistence.recipe.InMemoryRecipeRepository
import de.dhbw.mealplanner.persistence.user.InMemoryUserRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    val recipeRepository = InMemoryRecipeRepository()
    val mealPlanRepository = InMemoryMealPlanRepository()
    val userRepository = InMemoryUserRepository()

    val shoppingListGenerator = ShoppingListGenerator(recipeRepository)

    val generateShoppingListUseCase = GenerateShoppingListUseCase(mealPlanRepository, shoppingListGenerator)
    val assignRecipeToMealUseCase = AssignRecipeToMealUseCase(mealPlanRepository, recipeRepository)

    registerRoutes(
        recipeRepository = recipeRepository,
        mealPlanRepository = mealPlanRepository,
        userRepository = userRepository,
        generateShoppingListUseCase = generateShoppingListUseCase,
        assignRecipeToMealUseCase = assignRecipeToMealUseCase,
    )
}
