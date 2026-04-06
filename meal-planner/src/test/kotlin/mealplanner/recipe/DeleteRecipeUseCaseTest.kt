package de.dhbw.mealplanner.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.recipe.DeleteRecipeUseCase
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteRecipeUseCaseTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val useCase = DeleteRecipeUseCase(recipeRepository)

    @Test
    fun deleteRecipe() {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(recipeId, "Pasta")

        every { recipeRepository.findById(recipeId) } returns recipe
        every { recipeRepository.delete(recipeId) } just runs

        useCase.execute(recipeId)

        verify(exactly = 1) { recipeRepository.delete(recipeId) }
    }

    @Test
    fun throwNotFoundIfRecipeDoesNotExist() {
        val recipeId = RecipeId(UUID.randomUUID())
        every { recipeRepository.findById(recipeId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(recipeId)
        }

        verify(exactly = 0) { recipeRepository.delete(recipeId) }
    }
}
