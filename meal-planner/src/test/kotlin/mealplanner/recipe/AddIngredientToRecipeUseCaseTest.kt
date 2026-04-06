package de.dhbw.mealplanner.recipe

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.AddIngredientToRecipeUseCase
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.IngredientUnit
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class AddIngredientToRecipeUseCaseTest {

    private val recipeRepository = mockk<RecipeRepository>()
    private val useCase = AddIngredientToRecipeUseCase(recipeRepository)

    @Test
    fun addIngredientAndSaveRecipe() {
        val id = RecipeId(UUID.randomUUID())
        val recipe = Recipe(id, "Pasta")

        every { recipeRepository.findById(id) } returns recipe
        every { recipeRepository.save(any()) } just Runs

        useCase.execute(
            recipeId = id,
            ingredient = "Tomato",
            amount = 2.0,
            unit = "piece"
        )

        assertEquals(1, recipe.getIngredients().size)
        assertEquals("Tomato", recipe.getIngredients()[0].ingredient.value)
        assertEquals(IngredientUnit.PIECE, recipe.getIngredients()[0].unit)
        verify(exactly = 1) { recipeRepository.save(recipe) }
    }

    @Test
    fun throwNotFoundIfRecipeDoesNotExist() {
        val id = RecipeId(UUID.randomUUID())
        every { recipeRepository.findById(id) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(
                recipeId = id,
                ingredient = "Tomato",
                amount = 2.0,
                unit = "piece"
            )
        }

        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun throwValidationErrorIfIngredientAlreadyExists() {
        val id = RecipeId(UUID.randomUUID())
        val recipe = Recipe(id, "Pasta")

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Tomato"),
                amount = 2.0,
                unit = IngredientUnit.GRAM
            )
        )

        every { recipeRepository.findById(id) } returns recipe

        assertThrows(ValidationError::class.java) {
            useCase.execute(
                recipeId = id,
                ingredient = "Tomato",
                amount = 2.0,
                unit = "piece"
            )
        }

        verify(exactly = 0) { recipeRepository.save(any()) }
    }

    @Test
    fun throwValidationErrorIfUnitIsUnknown() {
        val id = RecipeId(UUID.randomUUID())

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(
                recipeId = id,
                ingredient = "Tomato",
                amount = 2.0,
                unit = "pieces"
            )
        }

        assertEquals("Unknown ingredient unit: pieces", exception.message)
        verify(exactly = 0) { recipeRepository.save(any()) }
    }
}
