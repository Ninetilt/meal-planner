package de.dhbw.mealplanner.domain.mealplan

import de.dhbw.mealplanner.domain.user.UserId
import java.util.UUID

class MealPlan(
    val id: MealPlanId,
    private var name: String,
    val createdBy: UserId,
    private val members: MutableSet<UserId> = mutableSetOf(),
    private val meals: MutableList<Meal> = mutableListOf()
) {

    init {
        require(name.isNotBlank()) { "MealPlan name must not be blank" }
        members.add(createdBy)
    }

    fun changeName(newName: String) {
        require(newName.isNotBlank()) { "MealPlan name must not be blank" }
        name = newName
    }

    fun addMember(userId: UserId) {
        members.add(userId)
    }

    fun removeMember(userId: UserId) {
        require(userId != createdBy) { "Creator cannot be removed from meal plan" }

        members.remove(userId)

        meals.forEach { meal ->
            meal.removeParticipant(userId)
            meal.removeResponsible(userId)
        }
    }

    fun getMembers(): Set<UserId> = members.toSet()

    fun getName(): String = name

    fun createMeal(date: MealDate, type: MealType): Meal {

        if (meals.any { it.date == date && it.type == type }) {
            throw IllegalArgumentException("Meal for this date and type already exists")
        }

        val meal = Meal(
            id = MealId(UUID.randomUUID()),
            date = date,
            type = type,
            recipeId = null
        )

        meals.add(meal)
        return meal
    }

    fun removeMeal(mealId: MealId) {
        meals.removeIf { it.id == mealId }
    }

    fun getMeals(): List<Meal> = meals.toList()

}