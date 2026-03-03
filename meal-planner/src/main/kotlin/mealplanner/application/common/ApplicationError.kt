package de.dhbw.mealplanner.application.common

sealed class ApplicationError(message: String) : RuntimeException(message)

class NotFoundError(entity: String) : ApplicationError("$entity not found")
class ValidationError(message: String) : ApplicationError(message)