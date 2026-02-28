package de.dhbw.mealplanner.domain.mealplan

interface MealPlanRepository {

    fun save(mealPlan: MealPlan)

    fun findById(id: MealPlanId): MealPlan?

    fun findAll(): List<MealPlan>

    fun delete(id: MealPlanId)
}