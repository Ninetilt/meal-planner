package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AddUserToMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealUseCase
import de.dhbw.mealplanner.application.recipe.RemoveIngredientFromRecipeUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveRecipeFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveUserFromMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetAllMealPlansUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlansForUserUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealUseCase
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.application.recipe.ChangeIngredientQuantityUseCase
import de.dhbw.mealplanner.application.recipe.ChangeRecipeDescriptionUseCase
import de.dhbw.mealplanner.application.recipe.CreateRecipeUseCase
import de.dhbw.mealplanner.application.recipe.query.GetAllRecipesUseCase
import de.dhbw.mealplanner.application.recipe.query.GetRecipeUseCase
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.registerRoutes(
    mealPlanRepository: MealPlanRepository,
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
    removeIngredientFromRecipeUseCase: RemoveIngredientFromRecipeUseCase,
    getMealPlanUseCase: GetMealPlanUseCase,
    getMealUseCase: GetMealUseCase,
    getRecipeUseCase: GetRecipeUseCase,
    getAllRecipesUseCase: GetAllRecipesUseCase,
    changeRecipeDescriptionUseCase: ChangeRecipeDescriptionUseCase,
    changeIngredientQuantityUseCase: ChangeIngredientQuantityUseCase,
    addUserToMealPlanUseCase: AddUserToMealPlanUseCase,
    removeUserFromMealPlanUseCase: RemoveUserFromMealPlanUseCase,
    getAllMealPlansUseCase: GetAllMealPlansUseCase,
    getMealPlansForUserUseCase: GetMealPlansForUserUseCase
) {
    routing {
        recipeRoutes(
            createRecipeUseCase,
            addIngredientToRecipeUseCase,
            removeIngredientFromRecipeUseCase,
            getRecipeUseCase,
            getAllRecipesUseCase,
            changeRecipeDescriptionUseCase,
            changeIngredientQuantityUseCase,
            )
        userRoutes(
            createUserUseCase,
            getMealPlansForUserUseCase
        )
        mealPlanRoutes(
            mealPlanRepository,
            assignRecipeToMealUseCase,
            addParticipantToMealUseCase,
            removeParticipantFromMealUseCase,
            assignResponsibleToMealUseCase,
            removeResponsibleFromMealUseCase,
            createMealPlanUseCase,
            createMealUseCase,
            removeRecipeFromMealUseCase,
            getMealPlanUseCase,
            getMealUseCase,
            addUserToMealPlanUseCase,
            removeUserFromMealPlanUseCase,
            getAllMealPlansUseCase,
            )
        shoppingRoutes(generateShoppingListUseCase)
    }
}