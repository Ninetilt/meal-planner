package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.user.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class MealPlanTest {

    private lateinit var mealPlan: MealPlan

    @BeforeEach
    fun setUp() {
        mealPlan = MealPlan(
            id = MealPlanId(UUID.randomUUID())
        )
    }

    @Test
    fun createMealSuccessfully() {
        val date = MealDate(LocalDate.of(2026, 1, 1))

        mealPlan.createMeal(date, MealType.LUNCH)

        val meals = mealPlan.getMeals()

        assertEquals(1, meals.size)
        assertEquals(date, meals[0].date)
        assertEquals(MealType.LUNCH, meals[0].type)
    }

    @Test
    fun createExistingMeal() {
        val date = MealDate(LocalDate.of(2026, 1, 1))

        mealPlan.createMeal(date, MealType.DINNER)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            mealPlan.createMeal(date, MealType.DINNER)
        }

        assertEquals("Meal for this date and type already exists", exception.message)
    }

    @Test
    fun removeMealSuccessfully() {
        val date = MealDate(LocalDate.of(2026, 1, 1))
        val meal = mealPlan.createMeal(date, MealType.BREAKFAST)

        mealPlan.removeMeal(meal.id)

        assertTrue(mealPlan.getMeals().isEmpty())
    }

    @Test
    fun addParticipantToMeal() {
        val date = MealDate(LocalDate.of(2026, 1, 1))
        val meal = mealPlan.createMeal(date, MealType.LUNCH)

        val userId = UserId(UUID.randomUUID())

        meal.addParticipant(userId)

        assertTrue(meal.getParticipants().contains(userId))
    }

    @Test
    fun assignResponsibleUserToMeal() {
        val date = MealDate(LocalDate.of(2026, 1, 1))
        val meal = mealPlan.createMeal(date, MealType.DINNER)

        val userId = UserId(UUID.randomUUID())

        meal.assignResponsible(userId)

        assertTrue(meal.getResponsibleUsers().contains(userId))
    }
}