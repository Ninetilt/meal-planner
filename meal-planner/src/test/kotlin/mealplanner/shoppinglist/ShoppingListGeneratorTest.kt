package de.dhbw.mealplanner.shoppinglist

import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.IngredientName
import de.dhbw.mealplanner.domain.recipe.IngredientQuantity
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import de.dhbw.mealplanner.domain.user.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class ShoppingListGeneratorTest {
    private lateinit var generator: ShoppingListGenerator
    private lateinit var mealPlan: MealPlan

    @BeforeEach
    fun setUp() {
        generator = ShoppingListGenerator()
        mealPlan = MealPlan(MealPlanId(UUID.randomUUID()))
    }

    @Test
    fun scaleIngredientsByNumberOfParticipants() {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Somethning tastsy"
        )

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Tomato"),
                amount = 2.0,
                unit = "pieces"
            )
        )

        val date = MealDate(LocalDate.of(2026, 3, 12))
        val meal = mealPlan.createMeal(date, MealType.LUNCH)
        meal.recipeId = recipeId

        meal.addParticipant(UserId(UUID.randomUUID()))
        meal.addParticipant(UserId(UUID.randomUUID()))

        val result = generator.generate(
            mealPlan = mealPlan,
            recipesById = mapOf(recipeId to recipe),
            startDate = LocalDate.of(2026, 3, 11),
            endDate = LocalDate.of(2026, 3, 13)
        )

        assertEquals(1, result.items.size)
        assertEquals(4.0, result.items[0].totalAmount)
        assertEquals(0, result.mealsWithoutParticipants)
    }

    @Test
    fun ignoreMealsWithoutParticipantsAndIncreaseCounter() {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Something more tastsy"
        )

        recipe.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Karotte"),
                amount = 2.0,
                unit = "Stück"
            )
        )

        val date = MealDate(LocalDate.of(2026, 2, 28))
        val meal = mealPlan.createMeal(date, MealType.DINNER)
        meal.recipeId = recipeId

        val result = generator.generate(
            mealPlan = mealPlan,
            recipesById = mapOf(recipeId to recipe),
            LocalDate.of(2026, 2, 27),
            LocalDate.of(2026, 3, 1)
        )

        assertTrue(result.items.isEmpty())
        assertEquals(1, result.mealsWithoutParticipants)
    }

    @Test
    fun reportRecipeWithoutIngredients() {
        val recipeId = RecipeId(UUID.randomUUID())
        val recipe = Recipe(
            id = recipeId,
            title = "Something empty"
        )

        val date = MealDate(LocalDate.of(2026, 2, 28))
        val meal = mealPlan.createMeal(date, MealType.DINNER)
        meal.recipeId = recipeId
        meal.addParticipant(UserId(UUID.randomUUID()))

        val result = generator.generate(
            mealPlan = mealPlan,
            recipesById = mapOf(recipeId to recipe),
            startDate = LocalDate.of(2026, 2, 27),
            endDate = LocalDate.of(2026, 3, 1)
        )

        assertEquals(1, result.recipesWithoutIngredients.size)
        assertEquals("Something empty", result.recipesWithoutIngredients[0])
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

        val date = MealDate(LocalDate.of(2026, 2, 28))
        val meal = mealPlan.createMeal(date, MealType.DINNER)
        meal.recipeId = recipeId
        meal.addParticipant(UserId(UUID.randomUUID()))

        val result = generator.generate(
            mealPlan,
            recipesById = mapOf(recipeId to recipe),
            LocalDate.of(2026, 2, 25),
            LocalDate.of(2026, 2, 26)
        )

        assertTrue(result.items.isEmpty())
    }

    @Test
    fun aggregateSameIngredientsWithSameUnitsFromDifferentRecipes() {
        val recipeId1 = RecipeId(UUID.randomUUID())
        val recipe1 = Recipe(
            id = recipeId1,
            title = "Recipe 1"
        )

        recipe1.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Potato"),
                amount = 100.0,
                unit = "g"
            )
        )

        val recipeId2 = RecipeId(UUID.randomUUID())
        val recipe2 = Recipe(
            id = recipeId2,
            title = "Recipe 2"
        )

        recipe2.addIngredient(
            IngredientQuantity(
                ingredient = IngredientName("Potato"),
                amount = 250.0,
                unit = "g"
            )
        )

        val meal1 = mealPlan.createMeal(
            MealDate(LocalDate.of(2026, 2, 2)),
            MealType.LUNCH
        )
        meal1.recipeId = recipeId1
        meal1.addParticipant(UserId(UUID.randomUUID()))

        val meal2 = mealPlan.createMeal(
            MealDate(LocalDate.of(2026, 2, 2)),
            MealType.DINNER
        )
        meal2.recipeId = recipeId2
        meal2.addParticipant(UserId(UUID.randomUUID()))

        val result = generator.generate(
            mealPlan = mealPlan,
            recipesById = mapOf(
                recipeId1 to recipe1,
                recipeId2 to recipe2
            ),
            startDate = LocalDate.of(2026, 2, 1),
            endDate = LocalDate.of(2026, 2, 3)
        )

        assertEquals(1, result.items.size)
        assertEquals("Potato", result.items[0].ingredient.value)
        assertEquals("g", result.items[0].unit)
        assertEquals(350.0, result.items[0].totalAmount)
    }
}
