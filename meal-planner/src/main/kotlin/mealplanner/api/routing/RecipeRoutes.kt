package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.recipe.AddIngredientRequest
import de.dhbw.mealplanner.api.dto.recipe.CreateRecipeRequest
import de.dhbw.mealplanner.api.dto.recipe.RecipeResponse
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import java.util.UUID

fun Route.recipeRoutes(recipeRepository: RecipeRepository) {

    route("/recipes") {

        post {
            val req = call.receive<CreateRecipeRequest>()

            if (req.title.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "title must not be blank")
                return@post
            }

            val recipe = Recipe(
                id = RecipeId(UUID.randomUUID()),
                title = req.title
            )

            recipeRepository.save(recipe)

            call.respond(
                HttpStatusCode.Created,
                RecipeResponse(
                    id = recipe.id.value.toString(),
                    title = recipe.getTitle()
                )
            )
        }

        post("/{id}/ingredients") {
            val idParam = call.parameters["id"]
            val uuid = runCatching { UUID.fromString(idParam) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid recipe id")

            val recipe = recipeRepository.findById(RecipeId(uuid))
                ?: return@post call.respond(HttpStatusCode.NotFound, "recipe not found")

            val req = call.receive<AddIngredientRequest>()

            if (req.ingredient.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "ingredient must not be blank")
            }
            if (req.unit.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "unit must not be blank")
            }
            if (req.amount <= 0) {
                return@post call.respond(HttpStatusCode.BadRequest, "amount must be > 0")
            }

            val ingredientQuantity = IngredientQuantity(
                ingredient = IngredientName(req.ingredient),
                amount = req.amount,
                unit = req.unit
            )

            try {
                recipe.addIngredient(ingredientQuantity)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "cannot add ingredient")
            }

            recipeRepository.save(recipe)

            call.respond(HttpStatusCode.Created)
        }

        get {
            val recipes = recipeRepository.findAll()
            call.respond(
                recipes.map {
                    RecipeResponse(
                        id = it.id.value.toString(),
                        title = it.getTitle()
                    )
                }
            )
        }

        get("/{id}") {
            val idParam = call.parameters["id"]
            val uuid = runCatching { UUID.fromString(idParam) }.getOrNull()
            if (uuid == null) {
                call.respond(HttpStatusCode.BadRequest, "invalid id")
                return@get
            }

            val recipe = recipeRepository.findById(RecipeId(uuid))
            if (recipe == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respond(
                RecipeResponse(
                    id = recipe.id.value.toString(),
                    title = recipe.getTitle()
                )
            )
        }
    }
}