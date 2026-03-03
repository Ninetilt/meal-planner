package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.AddParticipantRequest
import de.dhbw.mealplanner.api.dto.mealplan.AssignRecipeRequest
import de.dhbw.mealplanner.api.dto.mealplan.AssignResponsibleRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealPlanRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealRequest
import de.dhbw.mealplanner.api.dto.mealplan.MealDebugResponse
import de.dhbw.mealplanner.api.dto.mealplan.MealPlanDebugResponse
import de.dhbw.mealplanner.api.dto.mealplan.RemoveParticipantRequest
import de.dhbw.mealplanner.api.dto.mealplan.RemoveResponsibleRequest
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
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
    userRepository: UserRepository,
    assignRecipeToMealUseCase: AssignRecipeToMealUseCase,
    addParticipantToMealUseCase: AddParticipantToMealUseCase,
    removeParticipantFromMealUseCase: RemoveParticipantFromMealUseCase,
    assignResponsibleToMealUseCase: AssignResponsibleToMealUseCase,
    removeResponsibleFromMealUseCase: RemoveResponsibleFromMealUseCase
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
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val req = call.receive<AddParticipantRequest>()
            val userUuid = runCatching { UUID.fromString(req.userId) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid userId")

            try {
                addParticipantToMealUseCase.execute(
                    mealPlanId = MealPlanId(planUuid),
                    mealId = MealId(mealUuid),
                    userId = UserId(userUuid)
                )
            } catch (e: NotFoundError) {
                return@post call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/participants") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val req = call.receive<RemoveParticipantRequest>()
            val userUuid = runCatching { UUID.fromString(req.userId) }.getOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid userId")

            try {
                removeParticipantFromMealUseCase.execute(
                    mealPlanId = MealPlanId(planUuid),
                    mealId = MealId(mealUuid),
                    userId = UserId(userUuid)
                )
            } catch (e: NotFoundError) {
                return@delete call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val req = call.receive<AssignResponsibleRequest>()
            val userUuid = runCatching { UUID.fromString(req.userId) }.getOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "invalid userId")

            try {
                assignResponsibleToMealUseCase.execute(
                    mealPlanId = MealPlanId(planUuid),
                    mealId = MealId(mealUuid),
                    userId = UserId(userUuid)
                )
            } catch (e: NotFoundError) {
                return@post call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val req = call.receive<RemoveResponsibleRequest>()
            val userUuid = runCatching { UUID.fromString(req.userId) }.getOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "invalid userId")

            try {
                removeResponsibleFromMealUseCase.execute(
                    mealPlanId = MealPlanId(planUuid),
                    mealId = MealId(mealUuid),
                    userId = UserId(userUuid)
                )
            } catch (e: NotFoundError) {
                return@delete call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }

        put("/{planId}/meals/{mealId}/recipe") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val mealUuid = parseUuidParam(call.parameters["mealId"])
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid mealId")

            val req = call.receive<AssignRecipeRequest>()
            val recipeUuid = runCatching { UUID.fromString(req.recipeId) }.getOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, "invalid recipeId")

            try {
                assignRecipeToMealUseCase.execute(
                    mealPlanId = MealPlanId(planUuid),
                    mealId = MealId(mealUuid),
                    recipeId = RecipeId(recipeUuid)
                )
            } catch (e: NotFoundError) {
                return@put call.respond(HttpStatusCode.NotFound, e.message ?: "not found")
            }

            call.respond(HttpStatusCode.OK)
        }

        // erndpunkt erstmal nur für debugging
        get("/{planId}") {
            val planUuid = parseUuidParam(call.parameters["planId"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid planId")

            val plan = mealPlanRepository.findById(MealPlanId(planUuid))
                ?: return@get call.respond(HttpStatusCode.NotFound, "mealplan not found")

            val meals = plan.getMeals().map { meal ->
                MealDebugResponse(
                    id = meal.id.value.toString(),
                    date = meal.date.value.toString(),
                    type = meal.type.name,
                    recipeId = meal.recipeId?.value?.toString(),
                    participantCount = meal.getParticipants().size,
                    responsibleCount = meal.getResponsibleUsers().size
                )
            }

            val response = MealPlanDebugResponse(
                id = plan.id.value.toString(),
                mealCount = meals.size,
                meals = meals
            )

            call.respond(response)
        }
    }
}

private fun parseUuidParam(value: String?): UUID? =
    runCatching { UUID.fromString(value) }.getOrNull()