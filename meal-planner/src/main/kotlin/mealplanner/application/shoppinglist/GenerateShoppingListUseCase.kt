package de.dhbw.mealplanner.application.shoppinglist

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingList
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import java.time.LocalDate

class GenerateShoppingListUseCase(
    private val mealPlanRepository: MealPlanRepository,
    private val shoppingListGenerator: ShoppingListGenerator
) {

    fun execute(
        mealPlanId: MealPlanId,
        startDate: LocalDate,
        endDate: LocalDate
    ): ShoppingList {

        if (endDate.isBefore(startDate)) {
            throw ValidationError("endDate must be >= startDate")
        }

        val mealPlan = mealPlanRepository.findById(mealPlanId)
            ?: throw NotFoundError("mealplan")

        return shoppingListGenerator.generate(mealPlan, startDate, endDate)
    }
}