package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.application.common.ValidationError
import de.dhbw.mealplanner.application.mealplan.RemoveUserFromMealPlanUseCase
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.UUID

class RemoveUserFromMealPlanUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = RemoveUserFromMealPlanUseCase(mealPlanRepository, userRepository)

    @Test
    fun removeUserFromMealPlanAndSave() {
        val creatorId = UserId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())
        val mealPlanId = MealPlanId(UUID.randomUUID())

        val mealPlan = MealPlan(
            id = mealPlanId,
            name = "WG Plan",
            createdBy = creatorId
        )
        mealPlan.addMember(userId)

        val user = User(
            id = userId,
            name = "Anna",
            email = EmailAddress("anna@example.com"),
            password = "secret123"
        )

        every { mealPlanRepository.findById(mealPlanId) } returns mealPlan
        every { userRepository.findById(userId) } returns user
        every { mealPlanRepository.save(any()) } just runs

        useCase.execute(mealPlanId, userId)

        assertFalse(mealPlan.getMembers().contains(userId))
        verify(exactly = 1) { mealPlanRepository.save(mealPlan) }
    }

    @Test
    fun removingUserAlsoRemovesParticipantAndResponsible() {
        val creatorId = UserId(UUID.randomUUID())
        val userId = UserId(UUID.randomUUID())
        val mealPlanId = MealPlanId(UUID.randomUUID())

        val mealPlan = MealPlan(
            id = mealPlanId,
            name = "WG Plan",
            createdBy = creatorId
        )
        mealPlan.addMember(userId)

        val meal = mealPlan.createMeal(
            MealDate(LocalDate.of(2026, 1, 1)),
            MealType.DINNER
        )
        meal.addParticipant(userId)
        meal.assignResponsible(userId)

        val user = User(
            id = userId,
            name = "Anna",
            email = EmailAddress("anna@example.com"),
            password = "secret123"
        )

        every { mealPlanRepository.findById(mealPlanId) } returns mealPlan
        every { userRepository.findById(userId) } returns user
        every { mealPlanRepository.save(any()) } just runs

        useCase.execute(mealPlanId, userId)

        assertFalse(mealPlan.getMembers().contains(userId))
        assertFalse(meal.getParticipants().contains(userId))
        assertFalse(meal.getResponsibleUsers().contains(userId))
        verify(exactly = 1) { mealPlanRepository.save(mealPlan) }
    }

    @Test
    fun throwsValidationErrorWhenRemovingCreator() {
        val creatorId = UserId(UUID.randomUUID())
        val mealPlanId = MealPlanId(UUID.randomUUID())

        val mealPlan = MealPlan(
            id = mealPlanId,
            name = "WG Plan",
            createdBy = creatorId
        )

        val creator = User(
            id = creatorId,
            name = "Max",
            email = EmailAddress("max@example.com"),
            password = "secret123"
        )

        every { mealPlanRepository.findById(mealPlanId) } returns mealPlan
        every { userRepository.findById(creatorId) } returns creator

        val exception = assertThrows(ValidationError::class.java) {
            useCase.execute(mealPlanId, creatorId)
        }

        assertEquals("Creator cannot be removed from meal plan", exception.message)
        verify(exactly = 0) { mealPlanRepository.save(any()) }
    }
}