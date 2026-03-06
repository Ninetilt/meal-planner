package de.dhbw.mealplanner.domain.mealplan

import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.user.UserId

class Meal(
    val id: MealId,
    val date: MealDate,
    val type: MealType,
    var recipeId: RecipeId?,
    private val participants: MutableSet<UserId> = mutableSetOf(),
    private val responsibleUsers: MutableSet<UserId> = mutableSetOf()
) {

    fun assignRecipe(recipeId: RecipeId) {
        this.recipeId = recipeId
    }

    fun removeRecipe() {
        this.recipeId = null
    }

    fun addParticipant(userId: UserId) {
        participants.add(userId)
    }

    fun removeParticipant(userId: UserId) {
        participants.remove(userId)
        responsibleUsers.remove(userId)
    }

    fun assignResponsible(userId: UserId) {
        require(participants.contains(userId)) {
            "Only participants can be assigned as responsible"
        }
        responsibleUsers.add(userId)
    }

    fun removeResponsible(userId: UserId) {
        responsibleUsers.remove(userId)
    }

    fun getParticipants(): Set<UserId> = participants.toSet()

    fun getResponsibleUsers(): Set<UserId> = responsibleUsers.toSet()

    fun portionCount(): Int = participants.size
}