package de.dhbw.mealplanner.persistence.user

import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository

class InMemoryUserRepository : UserRepository {

    private val storage = mutableMapOf<UserId, User>()

    override fun save(user: User) {
        storage[user.id] = user
    }

    override fun findById(id: UserId): User? =
        storage[id]

    override fun findAll(): List<User> =
        storage.values.toList()

    override fun delete(id: UserId) {
        storage.remove(id)
    }
}