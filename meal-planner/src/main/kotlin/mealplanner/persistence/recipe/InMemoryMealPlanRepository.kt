package de.dhbw.mealplanner.persistence.recipe

import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository

class InMemoryMealPlanRepository : MealPlanRepository {

    private val storage = mutableMapOf<MealPlanId, MealPlan>()

    override fun save(mealPlan: MealPlan) {
        storage[mealPlan.id] = mealPlan
    }

    override fun findById(id: MealPlanId): MealPlan? =
        storage[id]

    override fun findAll(): List<MealPlan> =
        storage.values.toList()

    override fun delete(id: MealPlanId) {
        storage.remove(id)
    }
}