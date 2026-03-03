package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import de.dhbw.mealplanner.domain.user.UserRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.registerRoutes(
    recipeRepository: RecipeRepository,
    mealPlanRepository: MealPlanRepository,
    userRepository: UserRepository,
    generateShoppingListUseCase: GenerateShoppingListUseCase,
    assignRecipeToMealUseCase: AssignRecipeToMealUseCase
) {
    routing {
        recipeRoutes(recipeRepository)
        userRoutes(userRepository)
        mealPlanRoutes(mealPlanRepository, recipeRepository, userRepository, assignRecipeToMealUseCase)
        shoppingRoutes(generateShoppingListUseCase)
    }
}