package de.dhbw.mealplanner.application.mealplan.query

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class GetMealPlansForUserUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val userRepository: UserRepository
) {
    fun execute(userId: UserId): List<MealPlanListItemView> {
        val user = userRepository.findById(userId)
            ?: throw NotFoundError("user")

        return mealPlanRepository.findAll()
            .filter { it.isMember(user.id) }
            .map { mealPlan ->
                MealPlanListItemView(
                    id = mealPlan.id.value.toString(),
                    name = mealPlan.getName(),
                    createdBy = mealPlan.createdBy.value.toString(),
                    memberCount = mealPlan.getMembers().size,
                    mealCount = mealPlan.getMeals().size
                )
            }
            .sortedBy { it.name.lowercase() }
    }
}