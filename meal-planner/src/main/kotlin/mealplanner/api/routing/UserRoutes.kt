package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.mealplan.MealPlanListItemResponse
import de.dhbw.mealplanner.api.dto.user.CreateUserRequest
import de.dhbw.mealplanner.api.dto.user.DeleteUserResponse
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.mealplan.query.GetMealPlansForUserUseCase
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import de.dhbw.mealplanner.application.user.DeleteUserUseCase
import de.dhbw.mealplanner.domain.user.UserId
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(
    createUserUseCase: CreateUserUseCase,
    deleteUserUseCase: DeleteUserUseCase,
    getMealPlansForUserUseCase: GetMealPlansForUserUseCase
) {

    route("/users") {

        post {
            val req = call.receive<CreateUserRequest>()

            val id = createUserUseCase.execute(
                name = req.name,
                email = req.email,
                password = req.password
            )

            call.respond(HttpStatusCode.Created, IdResponse(id.value.toString()))
        }

        delete("/{userId}") {
            val userUuid = parseUuidParam(call.parameters["userId"], "userId")

            deleteUserUseCase.execute(UserId(userUuid))

            call.respond(
                HttpStatusCode.OK,
                DeleteUserResponse(id = userUuid.toString())
            )
        }

        get("/{userId}/mealplans") {
            val userUuid = parseUuidParam(call.parameters["userId"], "userId")
            val mealPlans = getMealPlansForUserUseCase.execute(UserId(userUuid))

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
