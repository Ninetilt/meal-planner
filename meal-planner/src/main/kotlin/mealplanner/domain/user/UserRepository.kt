package de.dhbw.mealplanner.domain.user

interface UserRepository {

    fun save(user: User)

    fun findById(id: UserId): User?

    fun findAll(): List<User>

    fun delete(id: UserId)
}