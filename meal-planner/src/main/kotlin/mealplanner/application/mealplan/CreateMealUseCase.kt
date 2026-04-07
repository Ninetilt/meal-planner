package de.dhbw.mealplanner.application.mealplan

import de.dhbw.mealplanner.api.dto.mealplan.CreateMealCommand
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import java.time.LocalDate

class CreateMealUseCase(
    private val mealPlanRepository: MealPlanRepository
) {
    fun execute(mealPlanId: MealPlanId, date: LocalDate, type: MealType): MealId =
        execute(CreateMealCommand(mealPlanId, date, type))

    fun execute(command: CreateMealCommand): MealId {
        val plan = mealPlanRepository.findById(command.mealPlanId)
            ?: throw NotFoundError("mealplan")

        val meal = plan.createMeal(MealDate(command.date), command.type)

        mealPlanRepository.save(plan)
        return meal.id
    }
}
