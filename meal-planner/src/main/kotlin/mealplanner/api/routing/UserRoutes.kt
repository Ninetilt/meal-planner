package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.user.CreateUserRequest
import de.dhbw.mealplanner.application.common.IdResponse
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.user.CreateUserUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(
    createUserUseCase: CreateUserUseCase
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
    }
}