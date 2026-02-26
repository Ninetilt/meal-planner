package de.dhbw.mealplanner.domain.mealplan

import de.dhbw.mealplanner.domain.recipe.RecipeId
import java.util.UUID

class Meal(
    val id: MealId,
    val date: MealDate,
    val type: MealType,
    var recipeId: RecipeId?,
    private val participants: MutableSet<UUID> = mutableSetOf(),
    private val responsibleUsers: MutableSet<UUID> = mutableSetOf()
) {

    fun assignRecipe(recipeId: RecipeId) {
        this.recipeId = recipeId
    }

    fun addParticipant(userId: UUID) {
        participants.add(userId)
    }

    fun removeParticipant(userId: UUID) {
        participants.remove(userId)
    }

    fun assignResponsible(userId: UUID) {
        responsibleUsers.add(userId)
    }

    fun removeResponsible(userId: UUID) {
        responsibleUsers.remove(userId)
    }

    fun getParticipants(): Set<UUID> = participants.toSet()

    fun getResponsibleUsers(): Set<UUID> = responsibleUsers.toSet()
}