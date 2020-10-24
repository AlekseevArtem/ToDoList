package ru.job4j.todolist.store

import ru.job4j.todolist.Task

interface IStore {
    fun getTasks(): List<Task>
    fun getFilteredTasks(filter: String): List<Task>
    fun addTask(name: String, description: String, created: String, photo: String?)
    fun editTask(id: Int, name: String, description: String, closed: String, photo: String)
    fun closeOrReopenTask(id: Int, closed: String?)
    fun deleteTask(id: Int): Int
    fun deleteAllTasks()
    fun findTaskByID(id: Int): Task?
}