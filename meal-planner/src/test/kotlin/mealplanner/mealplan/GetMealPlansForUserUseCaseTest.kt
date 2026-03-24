package de.dhbw.mealplanner.mealplan

import de.dhbw.mealplanner.application.mealplan.query.GetMealPlansForUserUseCase
import de.dhbw.mealplanner.application.common.NotFoundError
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class GetMealPlansForUserUseCaseTest {

    private val mealPlanRepository = mockk<MealPlanRepository>()
    private val userRepository = mockk<UserRepository>()
    private val useCase = GetMealPlansForUserUseCase(mealPlanRepository, userRepository)

    @Test
    fun returnOnlyMealPlansForUser() {
        val userId = UserId(UUID.randomUUID())
        val creatorId = UserId(UUID.randomUUID())

        val user = User(
            id = userId,
            name = "Anna",
            email = EmailAddress("anna@example.com"),
            password = "secret123"
        )

        val memberPlan = MealPlan(
            id = MealPlanId(UUID.randomUUID()),
            name = "WG Plan",
            createdBy = creatorId
        )
        memberPlan.addMember(userId)

        val otherPlan = MealPlan(
            id = MealPlanId(UUID.randomUUID()),
            name = "Familienplan",
            createdBy = creatorId
        )

        every { userRepository.findById(userId) } returns user
        every { mealPlanRepository.findAll() } returns listOf(memberPlan, otherPlan)

        val result = useCase.execute(userId)

        assertEquals(1, result.size)
        assertEquals("WG Plan", result[0].name)
    }
}