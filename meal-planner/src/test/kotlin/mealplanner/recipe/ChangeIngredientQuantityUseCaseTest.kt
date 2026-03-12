package de.dhbw.mealplanner.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.ChangeIngredientQuantityUseCase
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class ChangeIngredientQuantityUseCaseTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val useCase = ChangeIngredientQuantityUseCase(recipeRepository)

    @Test
    fun changeAmountAndUnitOfExistingIngredient() {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(recipeId, "Pasta")

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Tomato"),
                amount = 100.0,
                unit = "g"
            )
        )

        every { recipeRepository.findById(recipeId) } returns recipe
        every { recipeRepository.save(any()) } just runs

        useCase.execute(
            recipeId = recipeId,
            ingredient = "Tomato",
            amount = 2.0,
            unit = "pieces"
        )

        val changedIngredient = recipe.getIngredients().first()
        assertEquals("Tomato", changedIngredient.ingredient.value)
        assertEquals(2.0, changedIngredient.amount)
        assertEquals("pieces", changedIngredient.unit)

        verify(exactly = 1) { recipeRepository.save(recipe) }
    }

    @Test
    fun throwNotFoundIfRecipeDoesNotExist()  {
        val recipeId = RecipeId(UUID.randomUUID())
        every { recipeRepository.findById(recipeId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(recipeId, "Tomato", 250.0, "g")
        }

        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun throwValidationErrorIfIngredientDoesNotExist()  {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(recipeId, "Pasta")

        every { recipeRepository.findById(recipeId) } returns recipe

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(recipeId, "Tomato", 250.0, "g")
        }

        assertEquals("Ingredient does not exist", exception.message)
        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun throwValidationErrorIfAmountIsInvalid() {
        val recipeId = RecipeId(UUID.randomUUID())

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(recipeId, "Tomaten", 0.0, "g")
        }

        assertEquals("amount must be > 0", exception.message)
        verify(exactly = 0) { recipeRepository.save(any()) }
    }
}