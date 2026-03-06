package de.dhbw.mealplanner

import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.api.routing.registerRoutes
import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealUseCase
import de.dhbw.mealplanner.application.recipe.RemoveIngredientFromRecipeUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveRecipeFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealUseCase
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.application.recipe.ChangeRecipeDescriptionUseCase
import de.dhbw.mealplanner.application.recipe.CreateRecipeUseCase
import de.dhbw.mealplanner.application.recipe.query.GetAllRecipesUseCase
import de.dhbw.mealplanner.application.recipe.query.GetRecipeUseCase
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import de.dhbw.mealplanner.persistence.db.DatabaseFactory
import de.dhbw.mealplanner.persistence.mealplan.InMemoryMealPlanRepository
import de.dhbw.mealplanner.persistence.recipe.SqlRecipeRepository
import de.dhbw.mealplanner.persistence.user.SqlUserRepository
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

    DatabaseFactory.init()

    val recipeRepository = SqlRecipeRepository()
    val userRepository = SqlUserRepository()
    val mealPlanRepository = InMemoryMealPlanRepository()

    val shoppingListGenerator = ShoppingListGenerator(recipeRepository)

    val generateShoppingListUseCase = GenerateShoppingListUseCase(mealPlanRepository, shoppingListGenerator)
    val assignRecipeToMealUseCase = AssignRecipeToMealUseCase(mealPlanRepository, recipeRepository)
    val addParticipantToMealUseCase = AddParticipantToMealUseCase(mealPlanRepository, userRepository)
    val removeParticipantFromMealUseCase = RemoveParticipantFromMealUseCase(mealPlanRepository, userRepository)
    val assignResponsibleToMealUseCase = AssignResponsibleToMealUseCase(mealPlanRepository, userRepository)
    val removeResponsibleFromMealUseCase = RemoveResponsibleFromMealUseCase(mealPlanRepository, userRepository)
    val createUserUseCase = CreateUserUseCase(userRepository)
    val createRecipeUseCase = CreateRecipeUseCase(recipeRepository)
    val createMealPlanUseCase = CreateMealPlanUseCase(mealPlanRepository)
    val createMealUseCase = CreateMealUseCase(mealPlanRepository)
    val removeRecipeFromMealUseCase = RemoveRecipeFromMealUseCase(mealPlanRepository)
    val addIngredientToRecipeUseCase = AddIngredientToRecipeUseCase(recipeRepository)
    val removeIngredientFromRecipeUseCase = RemoveIngredientFromRecipeUseCase(recipeRepository)
    val getMealPlanUseCase = GetMealPlanUseCase(mealPlanRepository)
    val getMealUseCase = GetMealUseCase(mealPlanRepository)
    val getRecipeUseCase = GetRecipeUseCase(recipeRepository)
    val getAllRecipesUseCase = GetAllRecipesUseCase(recipeRepository)
    val changeRecipeDescriptionUseCase = ChangeRecipeDescriptionUseCase(recipeRepository)

    registerRoutes(
        mealPlanRepository = mealPlanRepository,
        generateShoppingListUseCase = generateShoppingListUseCase,
        assignRecipeToMealUseCase = assignRecipeToMealUseCase,
        addParticipantToMealUseCase = addParticipantToMealUseCase,
        removeParticipantFromMealUseCase = removeParticipantFromMealUseCase,
        assignResponsibleToMealUseCase = assignResponsibleToMealUseCase,
        removeResponsibleFromMealUseCase = removeResponsibleFromMealUseCase,
        createUserUseCase = createUserUseCase,
        createRecipeUseCase = createRecipeUseCase,
        createMealPlanUseCase = createMealPlanUseCase,
        createMealUseCase = createMealUseCase,
        removeRecipeFromMealUseCase = removeRecipeFromMealUseCase,
        addIngredientToRecipeUseCase = addIngredientToRecipeUseCase,
        removeIngredientFromRecipeUseCase = removeIngredientFromRecipeUseCase,
        getMealPlanUseCase = getMealPlanUseCase,
        getMealUseCase = getMealUseCase,
        getRecipeUseCase = getRecipeUseCase,
        getAllRecipesUseCase = getAllRecipesUseCase,
        changeRecipeDescriptionUseCase = changeRecipeDescriptionUseCase,
    )
}
