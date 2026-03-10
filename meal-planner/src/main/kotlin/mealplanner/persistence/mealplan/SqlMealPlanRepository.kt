package de.dhbw.mealplanner.persistence.mealplan

import de.dhbw.mealplanner.domain.mealplan.Meal
import de.dhbw.mealplanner.domain.mealplan.MealDate
import de.dhbw.mealplanner.domain.mealplan.MealId
import de.dhbw.mealplanner.domain.mealplan.MealPlan
import de.dhbw.mealplanner.domain.mealplan.MealPlanId
import de.dhbw.mealplanner.domain.mealplan.MealPlanRepository
import de.dhbw.mealplanner.domain.mealplan.MealType
import de.dhbw.mealplanner.domain.recipe.RecipeId
import de.dhbw.mealplanner.domain.user.UserId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SqlMealPlanRepository : MealPlanRepository {

    override fun save(mealPlan: MealPlan) {
        transaction {
            val existingMealPlan = MealPlansTable.selectAll()
                .where { MealPlansTable.id eq mealPlan.id.value }
                .singleOrNull()

            if (existingMealPlan == null) {
                MealPlansTable.insert {
                    it[id] = mealPlan.id.value
                }
            }

            val existingMealIds = MealsTable.selectAll()
                .where { MealsTable.mealPlanId eq mealPlan.id.value }
                .map { it[MealsTable.id].value }

            if (existingMealIds.isNotEmpty()) {
                MealParticipantsTable.deleteWhere {
                    MealParticipantsTable.mealId inList existingMealIds
                }

                MealResponsiblesTable.deleteWhere {
                    MealResponsiblesTable.mealId inList existingMealIds
                }

                MealsTable.deleteWhere {
                    MealsTable.mealPlanId eq mealPlan.id.value
                }
            }

            mealPlan.getMeals().forEach { meal ->
                MealsTable.insert {
                    it[id] = meal.id.value
                    it[mealPlanId] = mealPlan.id.value
                    it[date] = meal.date.value
                    it[type] = meal.type.name
                    it[recipeId] = meal.recipeId?.value
                }

                meal.getParticipants().forEach { userId ->
                    MealParticipantsTable.insert {
                        it[mealId] = meal.id.value
                        it[MealParticipantsTable.userId] = userId.value
                    }
                }

                meal.getResponsibleUsers().forEach { userId ->
                    MealResponsiblesTable.insert {
                        it[mealId] = meal.id.value
                        it[MealResponsiblesTable.userId] = userId.value
                    }
                }
            }
        }
    }

    override fun findById(id: MealPlanId): MealPlan? {
        return transaction {
            val mealPlanRow = MealPlansTable.selectAll()
                .where { MealPlansTable.id eq id.value }
                .singleOrNull()
                ?: return@transaction null

            val meals = MealsTable.selectAll()
                .where { MealsTable.mealPlanId eq mealPlanRow[MealPlansTable.id].value }
                .map { mealRow ->
                    val mealId = MealId(mealRow[MealsTable.id].value)

                    val participants = MealParticipantsTable.selectAll()
                        .where { MealParticipantsTable.mealId eq mealId.value }
                        .map { UserId(it[MealParticipantsTable.userId].value) }
                        .toMutableSet()

                    val responsibles = MealResponsiblesTable.selectAll()
                        .where { MealResponsiblesTable.mealId eq mealId.value }
                        .map { UserId(it[MealResponsiblesTable.userId].value) }
                        .toMutableSet()

                    Meal(
                        id = mealId,
                        date = MealDate(mealRow[MealsTable.date]),
                        type = MealType.valueOf(mealRow[MealsTable.type]),
                        recipeId = mealRow[MealsTable.recipeId]?.value?.let { RecipeId(it) },
                        participants = participants,
                        responsibleUsers = responsibles
                    )
                }
                .toMutableList()

            MealPlan(
                id = MealPlanId(mealPlanRow[MealPlansTable.id].value),
                meals = meals
            )
        }
    }

    override fun findAll(): List<MealPlan> {
        return transaction {
            MealPlansTable.selectAll()
                .map { row ->
                    val mealPlanId = MealPlanId(row[MealPlansTable.id].value)
                    findById(mealPlanId)
                }
                .filterNotNull()
        }
    }

    override fun delete(id: MealPlanId) {
        transaction {
            val mealIds = MealsTable.selectAll()
                .where { MealsTable.mealPlanId eq id.value }
                .map { it[MealsTable.id].value }

            if (mealIds.isNotEmpty()) {
                MealParticipantsTable.deleteWhere {
                    MealParticipantsTable.mealId inList mealIds
                }

                MealResponsiblesTable.deleteWhere {
                    MealResponsiblesTable.mealId inList mealIds
                }
            }

            MealsTable.deleteWhere {
                MealsTable.mealPlanId eq id.value
            }

            MealPlansTable.deleteWhere {
                MealPlansTable.id eq id.value
            }
        }
    }
}