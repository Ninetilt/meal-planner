package de.dhbw.mealplanner.application.mealplan


import de.dhbw.mealplanner.api.dto.mealplan.CreateMealPlanCommand
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import java.util.UUID

class CreateMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(name: String, createdBy: UserId): MealPlanId = execute(
        CreateMealPlanCommand(name, createdBy)
    )

    fun execute(command: CreateMealPlanCommand): MealPlanId {
        if (command.name.isBlank()) {
            throw ValidationError("meal plan name must not be blank")
        }

        val creator = userRepository.findById(command.createdBy)
            ?: throw NotFoundError("user")

        val mealPlan = MealPlan(
            id = MealPlanId(UUID.randomUUID()),
            name = command.name.trim(),
            createdBy = creator.id
        )

        mealPlanRepository.save(mealPlan)

        return mealPlan.id
    }
}
