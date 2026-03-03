package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
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
    assignRecipeToMealUseCase: AssignRecipeToMealUseCase,
    addParticipantToMealUseCase: AddParticipantToMealUseCase,
    removeParticipantFromMealUseCase: RemoveParticipantFromMealUseCase,
    assignResponsibleToMealUseCase: AssignResponsibleToMealUseCase,
    removeResponsibleFromMealUseCase: RemoveResponsibleFromMealUseCase
) {
    routing {
        recipeRoutes(recipeRepository)
        userRoutes(userRepository)
        mealPlanRoutes(
            mealPlanRepository,
            recipeRepository,
            userRepository,
            assignRecipeToMealUseCase,
            addParticipantToMealUseCase,
            removeParticipantFromMealUseCase,
            assignResponsibleToMealUseCase,
            removeResponsibleFromMealUseCase,
            )
        shoppingRoutes(generateShoppingListUseCase)
    }
}