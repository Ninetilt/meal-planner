package de.dhbw.mealplanner.user

import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class UserTest {

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = User(
            id = UserId(UUID.randomUUID()),
            name = "Mario"
        )
    }

    @Test
    fun getNameReturnsCorrectName() {
        assertEquals("Mario", user.getName())
    }

    @Test
    fun changeNameSuccessfully() {
        user.changeName("Luigi")
        assertEquals("Luigi", user.getName())
    }
}