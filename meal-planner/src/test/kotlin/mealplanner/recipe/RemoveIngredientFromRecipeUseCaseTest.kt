package de.dhbw.mealplanner.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.recipe.RemoveIngredientFromRecipeUseCase
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class RemoveIngredientFromRecipeUseCaseTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val useCase = RemoveIngredientFromRecipeUseCase(recipeRepository)

    @Test
    fun removeIngredientAndSaveRecipe() {
        val id = RecipeId(UUID.randomUUID())
        val recipe = Recipe(id, "Pasta")

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Tomato"),
                amount = 2.0,
                unit = "pieces"
            )
        )

        every { recipeRepository.findById(id) } returns recipe
        every { recipeRepository.save(any()) } just Runs

        useCase.execute(id, "Tomato")

        assertTrue(recipe.getIngredients().isEmpty())
        verify(exactly = 1) { recipeRepository.save(recipe) }
    }

    @Test
    fun removeNonExistingIngredientDoesNothing() {
        val id = RecipeId(UUID.randomUUID())
        val recipe = Recipe(id, "Pasta")

        every { recipeRepository.findById(id) } returns recipe
        every { recipeRepository.save(any()) } just Runs

        useCase.execute(id, "Tomato")

        assertTrue(recipe.getIngredients().isEmpty())
        verify(exactly = 1) { recipeRepository.save(recipe) }
    }

    @Test
    fun throwNotFoundIfRecipeDoesNotExist() {
        val id = RecipeId(UUID.randomUUID())
        every { recipeRepository.findById(id) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(id, "Tomato")
        }

        verify(exactly = 0) { recipeRepository.save(any()) }
    }
}