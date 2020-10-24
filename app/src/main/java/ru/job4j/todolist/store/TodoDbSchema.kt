package ru.job4j.todolist.store

class TodoDbSchema {
    object TodoTable {
        const val NAME = "todo"

        object Cols {
            const val NAME = "name"
            const val DESC = "desc"
            const val CREATED = "created"
            const val CLOSED = "closed"
            const val PHOTO = "photo"
        }
    }
}