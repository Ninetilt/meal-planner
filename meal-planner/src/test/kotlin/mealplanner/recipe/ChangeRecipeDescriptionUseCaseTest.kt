package com.deinname.mealplanner.application.recipe


import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.recipe.ChangeRecipeDescriptionUseCase
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class ChangeRecipeDescriptionUseCaseTest {

    private val repo = mockk<RecipeRepository>()
    private val useCase = ChangeRecipeDescriptionUseCase(repo)

    @Test
    fun changeDescriptionAndSaveRecipe() {
        val id = RecipeId(UUID.randomUUID())
        val recipe = Recipe(id, "Pasta")

        every { repo.findById(id) } returns recipe
        every { repo.save(any()) } just Runs

        useCase.execute(id, "step1: boil water")

        assertEquals("step1: boil water", recipe.getDescription())
        verify(exactly = 1) { repo.save(recipe) }
    }

    @Test
    fun thrwoNotFoundIfRecipeDoesNotExist() {
        val id = RecipeId(UUID.randomUUID())
        every { repo.findById(id) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(id, "desc")
        }

        verify(exactly = 0) { repo.save(any()) }
    }
}