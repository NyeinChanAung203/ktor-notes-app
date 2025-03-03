package com.example.entities

import com.example.db.RefreshTokenTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RefreshTokenEntity (id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<RefreshTokenEntity>(RefreshTokenTable)

    var userId by UsersEntity referencedOn RefreshTokenTable.userId
    var token by RefreshTokenTable.token
    var expiredAt by RefreshTokenTable.expiredAt
}