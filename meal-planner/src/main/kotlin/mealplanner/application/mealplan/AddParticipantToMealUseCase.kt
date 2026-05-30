package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.mealplan.commands.AddParticipantCommand
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class AddParticipantToMealUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId, userId: UserId) = execute(
        AddParticipantCommand(mealPlanId, mealId, userId)
    )

    fun execute(command: AddParticipantCommand) {
        val plan = mealPlanRepository.findById(command.mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.getMeals().find { it.id == command.mealId }
            ?: throw NotFoundError("meal")

        val user = userRepository.findById(command.userId)
            ?: throw NotFoundError("user")

        if (!plan.isMember(user.id)) {
            throw ValidationError("user is not a member of this meal plan")
        }

        meal.addParticipant(user.id)

        mealPlanRepository.save(plan)
    }
}
