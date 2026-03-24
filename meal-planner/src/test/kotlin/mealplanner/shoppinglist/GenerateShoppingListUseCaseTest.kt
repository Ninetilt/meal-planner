package de.dhbw.mealplanner.shoppinglist

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.Recipe
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.recipe.RecipeRepository
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingList
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import de.dhbw.mealplanner.domain.user.UserId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class GenerateShoppingListUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val recipeRepository = mockk<RecipeRepository>()
    private val generator = mockk<ShoppingListGenerator>()
    private val useCase = GenerateShoppingListUseCase(
        mealPlanRepository,
        recipeRepository,
        generator
    )

    @Test
    fun throwValidationErrorWhenEndDateisSmallerThanStartDate() {
        val ex = assertThrows(ValidationError::class.java) {
            useCase.execute(
                mealPlanId = MealPlanId(UUID.randomUUID()),
                startDate = LocalDate.of(2026, 3, 3),
                endDate = LocalDate.of(2026, 3, 2)
            )
        }

        assertEquals("endDate must be >= startDate", ex.message)
    }

    @Test
    fun throwNotFoundWhenMealPlanDoesNotExist() {
        val id = MealPlanId(UUID.randomUUID())
        every { mealPlanRepository.findById(id) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(id, LocalDate.of(2026, 3, 3),
                LocalDate.of(2026, 3, 4))
        }
    }


    @Test
    fun loadRecipeAndDelegateToGenerator() {
        val planId = MealPlanId(UUID.randomUUID())
        val recipeId = RecipeId(UUID.randomUUID())

        val mealPlan = MealPlan(
            id = planId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )
        val meal = mealPlan.createMeal(
            MealDate(LocalDate.of(2026, 1, 1)), MealType.LUNCH)
        meal.recipeId = recipeId

        val recipe = Recipe(recipeId, "Pasta")
        val expected = ShoppingList(emptyList(), emptyList(), 0)

        every { mealPlanRepository.findById(planId) } returns mealPlan
        every { recipeRepository.findById(recipeId) } returns recipe
        every {
            generator.generate(
                mealPlan,
                mapOf(recipeId to recipe),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 7)
            )
        } returns expected

        val result = useCase.execute(
            planId,
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 7)
        )

        assertSame(expected, result)

        verify(exactly = 1) { recipeRepository.findById(recipeId) }
        verify(exactly = 1) {
            generator.generate(
                mealPlan,
                mapOf(recipeId to recipe),
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 7)
            )
        }
    }
}
