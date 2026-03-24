package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.MealPlanListItemResponse
import de.dhbw.mealplanner.api.dto.user.CreateUserRequest
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlansForUserUseCase
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import de.dhbw.mealplanner.domain.user.UserId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(
    createUserUseCase: CreateUserUseCase,
    getMealPlansForUserUseCase: GetMealPlansForUserUseCase
) {

    route("/users") {

        post {
            val req = call.receive<CreateUserRequest>()

            val id = try {
                createUserUseCase.execute(
                    name = req.name,
                    email = req.email,
                    password = req.password
                )
            } catch (e: ValidationError) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "validation error")
            }

            call.respond(HttpStatusCode.Created, IdResponse(id.value.toString()))
        }

        get("/{userId}/mealplans") {
            val userUuid = parseUuidParam(call.parameters["userId"])
                ?: return@get call.respond(HttpStatusCode.BadRequest, "invalid userId")

            val mealPlans = try {
                getMealPlansForUserUseCase.execute(UserId(userUuid))
            } catch (e: NotFoundError) {
                return@get call.respond(
                    HttpStatusCode.NotFound,
                    e.message ?: "not found"
                )
            }

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
    }
}