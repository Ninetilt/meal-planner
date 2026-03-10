package de.dhbw.mealplanner.persistence.user

import de.dhbw.mealplanner.domain.user.EmailAddress
import de.dhbw.mealplanner.domain.user.User
import de.dhbw.mealplanner.domain.user.UserId
import de.dhbw.mealplanner.domain.user.UserRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

class SqlUserRepository : UserRepository {

    override fun save(user: User) {
        transaction {
            val existing = UsersTable.selectAll()
                .where { UsersTable.id eq user.id.value }
                .singleOrNull()

            if (existing == null) {
                UsersTable.insert {
                    it[id] = user.id.value
                    it[name] = user.getName()
                    it[email] = user.getEmail().value
                    it[password] = user.getPassword()
                }
            } else {
                UsersTable.update({ UsersTable.id eq user.id.value }) {
                    it[name] = user.getName()
                    it[email] = user.getEmail().value
                    it[password] = user.getPassword()
                }
            }
        }
    }

    override fun findById(id: UserId): User? {
        return transaction {
            val row = UsersTable.selectAll()
                .where { UsersTable.id eq id.value }
                .singleOrNull()
                ?: return@transaction null

            User(
                id = UserId(row[UsersTable.id].value),
                name = row[UsersTable.name],
                email = EmailAddress(row[UsersTable.email]),
                password = row[UsersTable.password]
            )
        }
    }

    override fun findByEmail(email: EmailAddress): User? {
        return transaction {
            val row = UsersTable.selectAll()
                .where { UsersTable.email eq email.value }
                .singleOrNull()
                ?: return@transaction null

            User(
                id = UserId(row[UsersTable.id].value),
                name = row[UsersTable.name],
                email = EmailAddress(row[UsersTable.email]),
                password = row[UsersTable.password]
            )
        }
    }

    override fun findAll(): List<User> {
        return transaction {
            UsersTable.selectAll()
                .map { row ->
                    User(
                        id = UserId(row[UsersTable.id].value),
                        name = row[UsersTable.name],
                        email = EmailAddress(row[UsersTable.email]),
                        password = row[UsersTable.password]
                    )
                }
        }
    }

    override fun delete(id: UserId) {
        transaction {
            UsersTable.deleteWhere { UsersTable.id eq id.value }
        }
    }
}