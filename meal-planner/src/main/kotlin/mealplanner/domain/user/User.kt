package de.dhbw.mealplanner.domain.user

class User(
    val id: UserId,
    private var name: String
) {
    init {
        require(name.isNotBlank()) { "User name must not be blank" }
    }

    fun changeName(newName: String) {
        require(newName.isNotBlank()) { "User name must not be blank" }
        name = newName
    }

    fun getName(): String = name
}