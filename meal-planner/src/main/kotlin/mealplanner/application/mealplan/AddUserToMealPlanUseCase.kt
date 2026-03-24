package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class AddUserToMealPlanUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(mealPlanId: MealPlanId, userId: UserId) {
        val mealPlan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        val user = userRepository.findById(userId)
            ?: throw NotFoundError("user")

        mealPlan.addMember(user.id)

        mealPlanRepository.save(mealPlan)
    }
}