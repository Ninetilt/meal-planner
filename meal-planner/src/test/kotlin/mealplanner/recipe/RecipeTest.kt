package de.dhbw.mealplanner.recipe

import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class RecipeTest {

    private lateinit var recipe: Recipe

    @BeforeEach
    fun setUp() {
        recipe = Recipe(
            id = RecipeId(UUID.randomUUID()),
            title = "Pasta",
            ingredients = mutableListOf(),
            preparationSteps = mutableListOf()
        )
    }

    @Test
    fun changeTitleIfValid() {
        recipe.changeTitle("Risotto")

        assertEquals("Risotto", recipe.getTitle())
    }

    @Test
    fun changeTitleBlank() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            recipe.changeTitle("")
        }

        assertEquals("Titel darf nicht leer sein", exception.message)
    }

    @Test
    fun addIngredientSuccessfully() {
        val ingredient = IngredientQuantity(
            ingredient = IngredientName("Tomate"),
            amount = 2.0,
            unit = "Stück"
        )

        recipe.addIngredient(ingredient)

        assertEquals(1, recipe.getIngredients().size)
        assertTrue(recipe.getIngredients().contains(ingredient))
    }

    @Test
    fun addIngredientAlreadyExistingIngredient() {
        val ingredient = IngredientQuantity(
            ingredient = IngredientName("Tomate"),
            amount = 2.0,
            unit = "Stück"
        )

        recipe.addIngredient(ingredient)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            recipe.addIngredient(ingredient)
        }

        assertEquals("Ingredient already exists", exception.message)
    }

    @Test
    fun removeIngredientSuccessfully() {
        val ingredient = IngredientQuantity(
            ingredient = IngredientName("Tomate"),
            amount = 2.0,
            unit = "Stück"
        )

        recipe.addIngredient(ingredient)

        recipe.removeIngredient(IngredientName("Tomate"))

        assertTrue(recipe.getIngredients().isEmpty())
    }
}