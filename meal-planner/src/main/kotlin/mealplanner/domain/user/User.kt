package de.dhbw.mealplanner.domain.user

class User(
    val id: UserId,
    private var name: String,
    private var email: EmailAddress,
    private var password: String
) {
    init {
        require(name.isNotBlank()) { "User name must not be blank" }
        require(password.isNotBlank()) { "Password must not be blank" }
    }

    fun changeName(newName: String) {
        require(newName.isNotBlank()) { "User name must not be blank" }
        name = newName
    }

    fun changeEmail(newEmail: EmailAddress) {
        email = newEmail
    }

    fun changePassword(newPassword: String) {
        require(newPassword.isNotBlank()) { "Password must not be blank" }
        password = newPassword
    }

    fun getName(): String = name

    fun getEmail(): EmailAddress = email

    fun getPassword(): String = password
}