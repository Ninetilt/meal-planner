package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.RemoveIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.AddIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.CreateRecipeRequest
import de.dhbw.mealplanner.api.dto.recipe.IngredientResponse
import de.dhbw.mealplanner.api.dto.recipe.RecipeDetailsResponse
import de.dhbw.mealplanner.api.dto.recipe.RecipeResponse
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.RemoveIngredientFromRecipeUseCase
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.application.recipe.CreateRecipeUseCase
import de.dhbw.mealplanner.application.recipe.query.GetAllRecipesUseCase
import de.dhbw.mealplanner.application.recipe.query.GetRecipeUseCase
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import java.util.UUID

fun Route.recipeRoutes(
    createRecipeUseCase: CreateRecipeUseCase,
    addIngredientToRecipeUseCase: AddIngredientToRecipeUseCase,
    removeIngredientFromRecipeUseCase: RemoveIngredientFromRecipeUseCase,
    getRecipeUseCase: GetRecipeUseCase,
    getAllRecipesUseCase: GetAllRecipesUseCase
    ) {

    route("/recipes") {

        post {
            val req = call.receive<CreateRecipeRequest>()

            val recipeId = try {
                createRecipeUseCase.execute(req.title)
            } catch (e: ValidationError) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "validation error")
            }

            call.respond(HttpStatusCode.Created, IdResponse(recipeId.value.toString()))
        }

        get {
            val views = getAllRecipesUseCase.execute()
            call.respond(
                views.map {
                    RecipeResponse(
                        id = it.id,
                        title = it.title
                    )
                }
            )
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            val uuid = runCatching {UUID.fromString(idParam) }.getOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid recipe id")

            val view = try {
                getRecipeUseCase.execute(RecipeId(uuid))
            } catch (e: NotFoundError) {
                return@get call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            val response = RecipeDetailsResponse(
                id = view.id,
                title = view.title,
                ingredients = view.ingredients.map {
                    IngredientResponse(
                        ingredient = it.ingredient,
                        amount = it.amount,
                        unit = it.unit
                    )
                },
                preparationSteps = view.preparationSteps
            )
            call.respond(response)
        }

        post("/{id}/ingredients") {
            val idParam = call.parameters["id"]
            val uuid = runCatching { UUID.fromString(idParam) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid recipe id")

            val req = call.receive<AddIngredientRequest>()

            try {
                addIngredientToRecipeUseCase.execute(
                    recipeId = RecipeId(uuid),
                    ingredient = req.ingredient,
                    amount = req.amount,
                    unit = req.unit
                )
            } catch (e: ValidationError) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "validation error")
            } catch (e: NotFoundError) {
                return@post call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.Created)
        }

        delete("/{id}/ingredients") {
            val idParam = call.parameters["id"]
            val uuid = runCatching { UUID.fromString(idParam) }.getOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid recipe id")

            val req = call.receive<RemoveIngredientRequest>()

            try {
                removeIngredientFromRecipeUseCase.execute(
                    recipeId = RecipeId(uuid),
                    ingredient = req.ingredient
                )
            } catch (e: ValidationError) {
                return@delete call.respond(HttpStatusCode.BadRequest, e.message ?: "validation error")
            } catch (e: NotFoundError) {
                return@delete call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}