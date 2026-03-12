package de.dhbw.mealplanner.domain.user

@JvmInline
value class EmailAddress(val value: String) {
    init {
        require(value.isNotBlank()) { "Email must not be blank" }
        require(value.contains("@")) { "Email must contain @" }
    }
}