package de.dhbw.mealplanner.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class ShoppingListGeneratorTest {

    private lateinit var recipeRepository: RecipeRepository
    private lateinit var generator: ShoppingListGenerator
    private lateinit var mealPlan: MealPlan

    @BeforeEach
    fun setUp() {
        recipeRepository = mockk()
        generator = ShoppingListGenerator(recipeRepository)
        mealPlan = MealPlan(MealPlanId(UUID.randomUUID()))
    }

    @Test
    fun aggregateIngredientsFromMultipleRecipes() {

        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Something Tasty"
        )

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Tomato"),
                amount = 2.0,
                unit = "pieces"
            )
        )

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Mozzarella"),
                amount = 250.0,
                unit = "gramm"
            )
        )

        every { recipeRepository.findById(recipeId) } returns recipe

        val date = MealDate(LocalDate.of(2026, 2, 28))
        val meal = mealPlan.createMeal(date, MealType.LUNCH)
        meal.assignRecipe(recipeId)

        val result = generator.generate(
            mealPlan,
            LocalDate.of(2026, 2, 27),
            LocalDate.of(2026, 3, 1)
        )

        assertEquals(2, result.items.size)
        assertEquals(2.0, result.items[0].totalAmount)
        assertEquals("Tomato", result.items[0].ingredient.value)
        assertEquals(250.0, result.items[1].totalAmount)
        assertEquals("Mozzarella", result.items[1].ingredient.value)
    }

    @Test
    fun reportRecipeWithoutIngredients() {

        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Empty Recipe"
        )

        every { recipeRepository.findById(recipeId) } returns recipe

        val date = MealDate(LocalDate.of(2026, 2, 28))
        val meal = mealPlan.createMeal(date, MealType.DINNER)
        meal.assignRecipe(recipeId)

        val result = generator.generate(
            mealPlan,
            LocalDate.of(2026, 2, 27),
            LocalDate.of(2026, 3, 1)
        )

        assertEquals(1, result.recipesWithoutIngredients.size)
        assertEquals("Empty Recipe", result.recipesWithoutIngredients[0])
    }

    @Test
    fun ignoreMealsOutsideDateRange() {

        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Something else Tasty"
        )

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("tomato"),
                amount = 2.0,
                unit = "pieces"
            )
        )

        every { recipeRepository.findById(recipeId) } returns recipe

        val mealDate = MealDate(LocalDate.of(2026, 2, 28)) // außerhalb
        val meal = mealPlan.createMeal(mealDate, MealType.LUNCH)
        meal.assignRecipe(recipeId)

        val result = generator.generate(
            mealPlan,
            LocalDate.of(2026, 2, 25),
            LocalDate.of(2026, 2, 26)
        )

        assertTrue(result.items.isEmpty())
    }
}