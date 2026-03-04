package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveRecipeFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.application.recipe.CreateRecipeUseCase
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.application.user.CreateUserUseCase
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
    removeResponsibleFromMealUseCase: RemoveResponsibleFromMealUseCase,
    createUserUseCase: CreateUserUseCase,
    createRecipeUseCase: CreateRecipeUseCase,
    createMealPlanUseCase: CreateMealPlanUseCase,
    createMealUseCase: CreateMealUseCase,
    removeRecipeFromMealUseCase: RemoveRecipeFromMealUseCase,
    addIngredientToRecipeUseCase: AddIngredientToRecipeUseCase,
) {
    routing {
        recipeRoutes(
            recipeRepository,
            createRecipeUseCase,
            addIngredientToRecipeUseCase,
            )
        userRoutes(createUserUseCase)
        mealPlanRoutes(
            mealPlanRepository,
            recipeRepository,
            userRepository,
            assignRecipeToMealUseCase,
            addParticipantToMealUseCase,
            removeParticipantFromMealUseCase,
            assignResponsibleToMealUseCase,
            removeResponsibleFromMealUseCase,
            createMealPlanUseCase,
            createMealUseCase,
            removeRecipeFromMealUseCase,
            )
        shoppingRoutes(generateShoppingListUseCase)
    }
}