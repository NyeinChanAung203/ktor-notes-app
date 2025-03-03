package com.example.entities

import com.example.db.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UsersEntity(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<UsersEntity>(UsersTable)

    var username by UsersTable.username
    var email by UsersTable.email
    var password by UsersTable.password
    var createdAt by UsersTable.createdAt
}