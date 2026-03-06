package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository


class AssignResponsibleToMealUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(mealPlanId: MealPlanId, mealId: MealId, userId: UserId) {
        val plan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.getMeals().find { it.id == mealId }
            ?: throw NotFoundError("meal")

        val user = userRepository.findById(userId)
            ?: throw NotFoundError("user")

        try {
            meal.assignResponsible(user.id)
        } catch (e: IllegalArgumentException) {
            throw ValidationError(e.message ?: "invalid responsible assignment")
        }

        mealPlanRepository.save(plan)
    }
}