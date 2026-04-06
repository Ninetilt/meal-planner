package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.mealplan.DeleteMealPlanUseCase
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.UserId
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class DeleteMealPlanUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val useCase = DeleteMealPlanUseCase(mealPlanRepository)

    @Test
    fun deleteMealPlan() {
        val mealPlanId = MealPlanId(UUID.randomUUID())
        val mealPlan = MealPlan(
            id = mealPlanId,
            name = "Plan",
            createdBy = UserId(UUID.randomUUID()),
        )

        every { mealPlanRepository.findById(mealPlanId) } returns mealPlan
        every { mealPlanRepository.delete(mealPlanId) } just runs

        useCase.execute(mealPlanId)

        verify(exactly = 1) { mealPlanRepository.delete(mealPlanId) }
    }

    @Test
    fun throwNotFoundIfMealPlanNotFound() {
        val mealPlanId = MealPlanId(UUID.randomUUID())
        every { mealPlanRepository.findById(mealPlanId) } returns null

        assertThrows(NotFoundError::class.java) {
            useCase.execute(mealPlanId)
        }

        verify(exactly = 0) { mealPlanRepository.delete(mealPlanId) }
    }
}
