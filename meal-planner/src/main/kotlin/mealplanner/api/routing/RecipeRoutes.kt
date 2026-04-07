package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.RemoveIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.AddIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.ChangeDescriptionRequest
import de.dhbw.mealplanner.api.dto.recipe.CreateRecipeRequest
import de.dhbw.mealplanner.api.dto.recipe.DeleteRecipeResponse
import de.dhbw.mealplanner.api.dto.recipe.IngredientResponse
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

            val recipeId = createRecipeUseCase.execute(req.title)

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
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")
            val view = getRecipeUseCase.execute(RecipeId(uuid))

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
                description = view.description
            )
            call.respond(response)
        }

        delete("/{id}") {
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")
            deleteRecipeUseCase.execute(RecipeId(uuid))

            call.respond(
                HttpStatusCode.OK,
                DeleteRecipeResponse(id = uuid.toString())
            )
        }

        post("/{id}/ingredients") {
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")

            val req = call.receive<AddIngredientRequest>()

            addIngredientToRecipeUseCase.execute(
                recipeId = RecipeId(uuid),
                ingredient = req.ingredient,
                amount = req.amount,
                unit = req.unit
            )

            call.respond(HttpStatusCode.Created)
        }

        put("/{id}/ingredients") {
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")

            val req = call.receive<de.dhbw.mealplanner.api.dto.recipe.ChangeIngredientQuantityRequest>()

            changeIngredientQuantityUseCase.execute(
                recipeId = RecipeId(uuid),
                ingredient = req.ingredient,
                amount = req.amount,
                unit = req.unit
            )

            call.respond(io.ktor.http.HttpStatusCode.OK)
        }

        delete("/{id}/ingredients") {
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")

            val req = call.receive<RemoveIngredientRequest>()

            removeIngredientFromRecipeUseCase.execute(
                recipeId = RecipeId(uuid),
                ingredient = req.ingredient
            )

            call.respond(HttpStatusCode.OK)
        }

        put("/{id}/description") {
            val uuid = parseUuidParam(call.parameters["id"], "recipe id")

            val req = call.receive<ChangeDescriptionRequest>()

            changeRecipeDescriptionUseCase.execute(
                recipeId = RecipeId(uuid),
                description = req.description
            )

            call.respond(HttpStatusCode.OK)
        }
    }
}
