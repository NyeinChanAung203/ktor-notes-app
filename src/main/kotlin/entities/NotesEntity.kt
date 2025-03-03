package com.example.entities

import com.example.db.NotesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class NotesEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotesEntity>(NotesTable)

    var userId by UsersEntity referencedOn NotesTable.userId
    var title by NotesTable.title
    var body by NotesTable.body
    var createdAt by NotesTable.createdAt
    var updatedAt by NotesTable.updatedAt
}