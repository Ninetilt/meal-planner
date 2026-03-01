package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.AddParticipantRequest
import de.dhbw.mealplanner.api.dto.mealplan.AssignRecipeRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealPlanRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealRequest
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.util.UUID

fun Route.mealPlanRoutes(
    mealPlanRepository: MealPlanRepository,
    recipeRepository: RecipeRepository,
    userRepository: UserRepository
) {
    route("/mealplans") {

        post {
            call.receive<CreateMealPlanRequest>()
            val plan = MealPlan(MealPlanId(UUID.randomUUID()))
            mealPlanRepository.save(plan)
            call.respond(HttpStatusCode.Created, mapOf("id" to plan.id.value.toString()))
        }

        get {
            val plans = mealPlanRepository.findAll()
            call.respond(plans.map { mapOf("id" to it.id.value.toString(), "meals" to it.getMeals().size) })
        }

        post("/{planId}/meals") {
            val planId = parseUuidParam(call.parameters["planId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val plan = mealPlanRepository.findById(MealPlanId(planId))
                ?: return@post call.respond(HttpStatusCode.NotFound, "mealplan not found")

            val req = call.receive<CreateMealRequest>()
            val date = runCatching { LocalDate.parse(req.date) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid date (expected YYYY-MM-DD)")

            val type = runCatching { MealType.valueOf(req.type) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid type")

            val meal = runCatching { plan.createMeal(MealDate(date), type) }.getOrElse {
                return@post call.respond(HttpStatusCode.BadRequest, it.message ?: "cannot create meal")
            }

            mealPlanRepository.save(plan)

            call.respond(HttpStatusCode.Created, mapOf("mealId" to meal.id.value.toString()))
        }

        post("/{planId}/meals/{mealId}/participants") {
            val planId = parseUuidParam(call.parameters["planId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid planId")
            val mealId = parseUuidParam(call.parameters["mealId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val plan = mealPlanRepository.findById(MealPlanId(planId))
                ?: return@post call.respond(HttpStatusCode.NotFound, "mealplan not found")

            val meal = plan.getMeals().find { it.id.value == mealId }
                ?: return@post call.respond(HttpStatusCode.NotFound, "meal not found")

            val req = call.receive<AddParticipantRequest>()
            val userUuid = runCatching { UUID.fromString(req.userId) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid userId")

            val user = userRepository.findById(UserId(userUuid))
                ?: return@post call.respond(HttpStatusCode.NotFound, "user not found")

            meal.addParticipant(user.id)

            mealPlanRepository.save(plan)

            call.respond(HttpStatusCode.OK)
        }

        put("/{planId}/meals/{mealId}/recipe") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val plan = mealPlanRepository.findById(MealPlanId(planUuid))
                ?: return@put call.respond(HttpStatusCode.NotFound, "mealplan not found")

            val meal = plan.getMeals().find { it.id.value == mealUuid }
                ?: return@put call.respond(HttpStatusCode.NotFound, "meal not found")

            val req = call.receive<AssignRecipeRequest>()
            val recipeUuid = runCatching { UUID.fromString(req.recipeId) }.getOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid recipeId")

            val recipeExists = recipeRepository.findById(RecipeId(recipeUuid)) != null
            if (!recipeExists) {
                return@put call.respond(HttpStatusCode.NotFound, "recipe not found")
            }

            meal.recipeId = RecipeId(recipeUuid)

            mealPlanRepository.save(plan)

            call.respond(HttpStatusCode.OK)
        }
    }
}

private fun parseUuidParam(value: String?): UUID? =
    runCatching { UUID.fromString(value) }.getOrNull()