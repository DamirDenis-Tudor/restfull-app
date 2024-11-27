package org.pos.entities

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.varchar

object BlacklistTokens : Table<BlacklistToken>("blacklist") {
    val id = varchar("id").primaryKey().bindTo { it.id }
    val token = varchar("token").bindTo { it.token }
    val timestamp = datetime("timestamp").bindTo { it.timestamp }
}

interface BlacklistToken : Entity<BlacklistToken> {
    companion object : Entity.Factory<BlacklistToken>()

    var id: String
    var token: String
    var timestamp: java.time.LocalDateTime
}

fun Database.blacklistTokens() = this.sequenceOf(BlacklistTokens)