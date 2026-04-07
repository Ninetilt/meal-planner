package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.recipe.AddIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.ChangeDescriptionRequest
import de.dhbw.mealplanner.api.dto.recipe.ChangeIngredientQuantityRequest
import de.dhbw.mealplanner.api.dto.recipe.CreateRecipeRequest
import de.dhbw.mealplanner.api.dto.recipe.DeleteRecipeResponse
import de.dhbw.mealplanner.api.dto.recipe.RemoveIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.RecipeDetailsResponse
import de.dhbw.mealplanner.api.dto.recipe.RecipeResponse
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.recipe.RemoveIngredientFromRecipeUseCase
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.application.recipe.ChangeIngredientQuantityUseCase
import de.dhbw.mealplanner.application.recipe.ChangeRecipeDescriptionUseCase
import de.dhbw.mealplanner.application.recipe.CreateRecipeUseCase
import de.dhbw.mealplanner.application.recipe.DeleteRecipeUseCase
import de.dhbw.mealplanner.application.recipe.query.GetAllRecipesUseCase
import de.dhbw.mealplanner.application.recipe.query.GetRecipeUseCase
import de.dhbw.mealplanner.domain.recipe.RecipeId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.recipeRoutes(
    createRecipeUseCase: CreateRecipeUseCase,
    deleteRecipeUseCase: DeleteRecipeUseCase,
    addIngredientToRecipeUseCase: AddIngredientToRecipeUseCase,
    removeIngredientFromRecipeUseCase: RemoveIngredientFromRecipeUseCase,
    getRecipeUseCase: GetRecipeUseCase,
    getAllRecipesUseCase: GetAllRecipesUseCase,
    changeRecipeDescriptionUseCase: ChangeRecipeDescriptionUseCase,
    changeIngredientQuantityUseCase: ChangeIngredientQuantityUseCase,
    ) {

    route("/recipes") {

        post {
            val req = call.receive<CreateRecipeRequest>()
            val recipeId = createRecipeUseCase.execute(req.toCommand())
            call.respond(HttpStatusCode.Created, IdResponse(recipeId.value.toString()))
        }

        get {
            val views = getAllRecipesUseCase.execute()
            call.respond(views.map(RecipeResponse::from))
        }

        get("/{id}") {
            val uuid = call.requireUuidParam("id")
            val view = getRecipeUseCase.execute(RecipeId(uuid))
            call.respond(RecipeDetailsResponse.from(view))
        }

        delete("/{id}") {
            val uuid = call.requireUuidParam("id")
            deleteRecipeUseCase.execute(RecipeId(uuid))
            call.respond(
                HttpStatusCode.OK,
                DeleteRecipeResponse(id = uuid.toString())
            )
        }

        post("/{id}/ingredients") {
            val uuid = call.requireUuidParam("id")
            val req = call.receive<AddIngredientRequest>()
            addIngredientToRecipeUseCase.execute(req.toCommand(uuid))
            call.respond(HttpStatusCode.Created)
        }

        put("/{id}/ingredients") {
            val uuid = call.requireUuidParam("id")
            val req = call.receive<ChangeIngredientQuantityRequest>()
            changeIngredientQuantityUseCase.execute(req.toCommand(uuid))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}/ingredients") {
            val uuid = call.requireUuidParam("id")
            val req = call.receive<RemoveIngredientRequest>()
            removeIngredientFromRecipeUseCase.execute(req.toCommand(uuid))
            call.respond(HttpStatusCode.OK)
        }

        put("/{id}/description") {
            val uuid = call.requireUuidParam("id")
            val req = call.receive<ChangeDescriptionRequest>()
            changeRecipeDescriptionUseCase.execute(req.toCommand(uuid))
            call.respond(HttpStatusCode.OK)
        }
    }
}
