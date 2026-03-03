package de.dhbw.mealplanner.shoppinglist

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.shoppinglist.GenerateShoppingListUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingList
import de.dhbw.mealplanner.domain.shoppinglist.ShoppingListGenerator
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class GenerateShoppingListUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val generator = mockk<ShoppingListGenerator>()
    private val useCase = GenerateShoppingListUseCase(mealPlanRepository, generator)

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
    fun returnMealPlanIfExists() {
        val id = MealPlanId(UUID.randomUUID())
        val plan = MealPlan(id)
        val expected = ShoppingList(
            items = emptyList(),
            recipesWithoutIngredients = emptyList(),
            mealsWithoutParticipants = 0
        )

        every { mealPlanRepository.findById(id) } returns plan
        every { generator.generate(plan, any(), any()) } returns expected

        val result = useCase.execute(id, LocalDate.of(2026, 3, 3),
            LocalDate.of(2026, 3, 9))

        assertSame(expected, result)
        verify(exactly = 1) { generator.generate(plan, LocalDate.of(2026, 3, 3),
            LocalDate.of(2026, 3, 9)) }
    }
}