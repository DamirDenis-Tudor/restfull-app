package org.pos.entities

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Users : Table<User>("users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val password = varchar("password").bindTo { it.password }
    val role = int("role").bindTo { it.role }
}

interface User : Entity<User> {
    companion object : Entity.Factory<User>()

    val id: Int
    val email: String
    val password: String
    val role: Int
}

fun Database.users() = this.sequenceOf(Users)