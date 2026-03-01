package de.dhbw.mealplanner.api.routing

import de.dhbw.mealplanner.api.dto.user.CreateUserRequest
import de.dhbw.mealplanner.api.dto.user.UserResponse
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.userRoutes(userRepository: UserRepository) {

    route("/users") {

        post {
            val req = call.receive<CreateUserRequest>()
            if (req.name.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "name must not be blank")
                return@post
            }

            val user = User(UserId(UUID.randomUUID()), req.name)
            userRepository.save(user)

            call.respond(
                HttpStatusCode.Created,
                UserResponse(user.id.value.toString(), user.getName())
            )
        }

        get {
            val users = userRepository.findAll()
            call.respond(users.map { UserResponse(it.id.value.toString(), it.getName()) })
        }
    }
}