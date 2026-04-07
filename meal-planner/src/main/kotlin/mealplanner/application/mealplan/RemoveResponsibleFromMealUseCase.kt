package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.commands.RemoveResponsibleCommand
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class RemoveResponsibleFromMealUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId, userId: UserId) = execute(
        RemoveResponsibleCommand(mealPlanId, mealId, userId)
    )

    fun execute(command: RemoveResponsibleCommand) {
        val plan = mealPlanRepository.findById(command.mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.getMeals().find { it.id == command.mealId }
            ?: throw NotFoundError("meal")

        val user = userRepository.findById(command.userId)
            ?: throw NotFoundError("user")

        meal.removeResponsible(user.id)

        mealPlanRepository.save(plan)
    }
}
