package de.dhbw.mealplanner.persistence.db

import de.dhbw.mealplanner.persistence.mealplan.MealParticipantsTable
import de.dhbw.mealplanner.persistence.mealplan.MealPlansTable
import de.dhbw.mealplanner.persistence.mealplan.MealResponsiblesTable
import de.dhbw.mealplanner.persistence.mealplan.MealsTable
import de.dhbw.mealplanner.persistence.recipe.RecipeIngredientsTable
import de.dhbw.mealplanner.persistence.recipe.RecipesTable
import de.dhbw.mealplanner.persistence.user.UsersTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.Companion.connect(
            url = "jdbc:sqlite:mealplanner.db",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.create(
                RecipesTable,
                RecipeIngredientsTable,
                UsersTable,
                MealPlansTable,
                MealsTable,
                MealParticipantsTable,
                MealResponsiblesTable
            )
        }
    }
}