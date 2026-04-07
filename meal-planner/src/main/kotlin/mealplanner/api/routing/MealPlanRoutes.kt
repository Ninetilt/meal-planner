package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.AddParticipantRequest
import de.dhbw.mealplanner.api.dto.mealplan.AddUserToMealPlanRequest
import de.dhbw.mealplanner.api.dto.mealplan.AssignRecipeRequest
import de.dhbw.mealplanner.api.dto.mealplan.AssignResponsibleRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealPlanRequest
import de.dhbw.mealplanner.api.dto.mealplan.CreateMealRequest
import de.dhbw.mealplanner.api.dto.mealplan.DeleteMealPlanResponse
import de.dhbw.mealplanner.api.dto.mealplan.DeleteMealResponse
import de.dhbw.mealplanner.api.dto.mealplan.MealPlanListItemResponse
import de.dhbw.mealplanner.api.dto.mealplan.MealPlanResponse
import de.dhbw.mealplanner.api.dto.mealplan.MealResponse
import de.dhbw.mealplanner.api.dto.mealplan.RemoveParticipantRequest
import de.dhbw.mealplanner.api.dto.mealplan.RemoveResponsibleRequest
import de.dhbw.mealplanner.api.dto.mealplan.RemoveUserFromMealPlanRequest
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.mealplan.AddParticipantToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AddUserToMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.AssignRecipeToMealUseCase
import de.dhbw.mealplanner.application.mealplan.AssignResponsibleToMealUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.CreateMealUseCase
import de.dhbw.mealplanner.application.mealplan.DeleteMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.DeleteMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveParticipantFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveRecipeFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveResponsibleFromMealUseCase
import de.dhbw.mealplanner.application.mealplan.RemoveUserFromMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetAllMealPlansUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlanUseCase
import de.dhbw.mealplanner.application.mealplan.query.GetMealUseCase
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.user.UserId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mealPlanRoutes(
    assignRecipeToMealUseCase: AssignRecipeToMealUseCase,
    addParticipantToMealUseCase: AddParticipantToMealUseCase,
    removeParticipantFromMealUseCase: RemoveParticipantFromMealUseCase,
    assignResponsibleToMealUseCase: AssignResponsibleToMealUseCase,
    removeResponsibleFromMealUseCase: RemoveResponsibleFromMealUseCase,
    createMealPlanUseCase: CreateMealPlanUseCase,
    createMealUseCase: CreateMealUseCase,
    deleteMealPlanUseCase: DeleteMealPlanUseCase,
    deleteMealUseCase: DeleteMealUseCase,
    removeRecipeFromMealUseCase: RemoveRecipeFromMealUseCase,
    getMealPlanUseCase: GetMealPlanUseCase,
    getMealUseCase: GetMealUseCase,
    addUserToMealPlanUseCase: AddUserToMealPlanUseCase,
    removeUserFromMealPlanUseCase: RemoveUserFromMealPlanUseCase,
    getAllMealPlansUseCase: GetAllMealPlansUseCase
) {
    route("/mealplans") {

        post {
            val req = call.receive<CreateMealPlanRequest>()

            val creatorUuid = parseUuidParam(req.createdBy, "createdBy")

            val mealPlanId = createMealPlanUseCase.execute(
                name = req.name,
                createdBy = UserId(creatorUuid)
            )
            call.respond(HttpStatusCode.Created,IdResponse(mealPlanId.value.toString()))
        }

        get {
            val mealPlans = getAllMealPlansUseCase.execute()
            call.respond(
                mealPlans.map {
                    MealPlanListItemResponse(
                        id = it.id,
                        name = it.name,
                        createdBy = it.createdBy,
                        memberCount = it.memberCount,
                        mealCount = it.mealCount
                    )
                }
            )
        }

        get("/{planId}") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val view = getMealPlanUseCase.execute(MealPlanId(planUuid))

            val response = MealPlanResponse(
                id = view.id,
                name = view.name,
                createdBy = view.createdBy,
                memberCount = view.memberCount,
                mealCount = view.mealCount,
                meals = view.meals.map {
                    MealResponse(
                        id = it.id,
                        date = it.date,
                        type = it.type,
                        recipeId = it.recipeId,
                        participantCount = it.participantCount,
                        responsibleCount = it.responsibleCount
                    )
                }
            )

            call.respond(response)
        }

        delete("/{planId}") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            deleteMealPlanUseCase.execute(MealPlanId(planUuid))

            call.respond(
                HttpStatusCode.OK,
                DeleteMealPlanResponse(id = planUuid.toString())
            )
        }

        post("/{planId}/meals") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")

            val req = call.receive<CreateMealRequest>()

            val date = parseDateParam(req.date, "date")
            val type = parseEnumParam<MealType>(req.type, "type")
            val mealId = createMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                date = date,
                type = type
            )

            call.respond(HttpStatusCode.Created,IdResponse(mealId.value.toString()))
        }

        post("/{planId}/members") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")

            val req = call.receive<AddUserToMealPlanRequest>()

            val userUuid = parseUuidParam(req.userId, "userId")
            addUserToMealPlanUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/members") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")

            val req = call.receive<RemoveUserFromMealPlanRequest>()

            val userUuid = parseUuidParam(req.userId, "userId")
            removeUserFromMealPlanUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/{planId}/meals/{mealId}") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")
            val view = getMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid)
            )

            val response = MealResponse(
                id = view.id,
                date = view.date,
                type = view.type,
                recipeId = view.recipeId,
                participantCount = view.participantCount,
                responsibleCount = view.responsibleCount
            )

            call.respond(response)
        }

        delete("/{planId}/meals/{mealId}") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")
            deleteMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid)
            )

            call.respond(
                HttpStatusCode.OK,
                DeleteMealResponse(
                    id = mealUuid.toString(),
                    mealPlanId = planUuid.toString()
                )
            )
        }

        post("/{planId}/meals/{mealId}/participants") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")

            val req = call.receive<AddParticipantRequest>()
            val userUuid = parseUuidParam(req.userId, "userId")
            addParticipantToMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/participants") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")

            val req = call.receive<RemoveParticipantRequest>()
            val userUuid = parseUuidParam(req.userId, "userId")
            removeParticipantFromMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        post("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")

            val req = call.receive<AssignResponsibleRequest>()
            val userUuid = parseUuidParam(req.userId, "userId")
            assignResponsibleToMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")

            val req = call.receive<RemoveResponsibleRequest>()
            val userUuid = parseUuidParam(req.userId, "userId")
            removeResponsibleFromMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid),
                userId = UserId(userUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        put("/{planId}/meals/{mealId}/recipe") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")

            val req = call.receive<AssignRecipeRequest>()
            val recipeUuid = parseUuidParam(req.recipeId, "recipeId")
            assignRecipeToMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid),
                recipeId = RecipeId(recipeUuid)
            )

            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/recipe") {
            val planUuid = parseUuidParam(call.parameters["planId"], "planId")
            val mealUuid = parseUuidParam(call.parameters["mealId"], "mealId")
            removeRecipeFromMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid)
            )

            call.respond(HttpStatusCode.OK)
        }
    }
}
