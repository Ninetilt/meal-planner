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
import de.dhbw.mealplanner.api.dto.common.CreatedResourceResponse
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
            val mealPlanId = createMealPlanUseCase.execute(req.toCommand())
            call.respond(HttpStatusCode.Created,CreatedResourceResponse(mealPlanId.value.toString()))
        }

        get {
            val mealPlans = getAllMealPlansUseCase.execute()
            call.respond(mealPlans.map(MealPlanListItemResponse::from))
        }

        get("/{planId}") {
            val planUuid = call.requireUuidParam("planId")
            val view = getMealPlanUseCase.execute(MealPlanId(planUuid))
            call.respond(MealPlanResponse.from(view))
        }

        delete("/{planId}") {
            val planUuid = call.requireUuidParam("planId")
            deleteMealPlanUseCase.execute(MealPlanId(planUuid))
            call.respond(
                HttpStatusCode.OK,
                DeleteMealPlanResponse(id = planUuid.toString())
            )
        }

        post("/{planId}/meals") {
            val planUuid = call.requireUuidParam("planId")
            val req = call.receive<CreateMealRequest>()
            val mealId = createMealUseCase.execute(req.toCommand(planUuid))
            call.respond(HttpStatusCode.Created,CreatedResourceResponse(mealId.value.toString()))
        }

        post("/{planId}/members") {
            val planUuid = call.requireUuidParam("planId")
            val req = call.receive<AddUserToMealPlanRequest>()
            addUserToMealPlanUseCase.execute(req.toCommand(planUuid))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/members") {
            val planUuid = call.requireUuidParam("planId")
            val req = call.receive<RemoveUserFromMealPlanRequest>()
            removeUserFromMealPlanUseCase.execute(req.toCommand(planUuid))
            call.respond(HttpStatusCode.OK)
        }

        get("/{planId}/meals/{mealId}") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val view = getMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid)
            )
            call.respond(MealResponse.from(view))
        }

        delete("/{planId}/meals/{mealId}") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
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
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val req = call.receive<AddParticipantRequest>()
            addParticipantToMealUseCase.execute(req.toCommand(planUuid, mealUuid))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/participants") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val req = call.receive<RemoveParticipantRequest>()
            removeParticipantFromMealUseCase.execute(req.toCommand(planUuid, mealUuid))
            call.respond(HttpStatusCode.OK)
        }

        post("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val req = call.receive<AssignResponsibleRequest>()
            assignResponsibleToMealUseCase.execute(req.toCommand(planUuid, mealUuid))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/responsibles") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val req = call.receive<RemoveResponsibleRequest>()
            removeResponsibleFromMealUseCase.execute(req.toCommand(planUuid, mealUuid))
            call.respond(HttpStatusCode.OK)
        }

        put("/{planId}/meals/{mealId}/recipe") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            val req = call.receive<AssignRecipeRequest>()
            assignRecipeToMealUseCase.execute(req.toCommand(planUuid, mealUuid))
            call.respond(HttpStatusCode.OK)
        }

        delete("/{planId}/meals/{mealId}/recipe") {
            val planUuid = call.requireUuidParam("planId")
            val mealUuid = call.requireUuidParam("mealId")
            removeRecipeFromMealUseCase.execute(
                mealPlanId = MealPlanId(planUuid),
                mealId = MealId(mealUuid)
            )
            call.respond(HttpStatusCode.OK)
        }
    }
}
