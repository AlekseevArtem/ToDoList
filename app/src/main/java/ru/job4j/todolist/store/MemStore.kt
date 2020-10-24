package ru.job4j.todolist.store

import android.content.Context
import ru.job4j.todolist.Task
import java.util.*
import java.util.stream.Collectors

class MemStore : IStore {
    private var tasks: MutableList<Task> = ArrayList()
    private var counter = 0

    companion object {
        private val INST = MemStore()
        fun getInstance(context: Context?): MemStore {
            return INST
        }
    }

    override fun getTasks(): List<Task> = tasks

    override fun getFilteredTasks(filter: String): List<Task> {
        return tasks.stream().filter { (_, name, desc, created) ->
            name.toLowerCase().contains(filter.toLowerCase()) ||
                    desc.toLowerCase().contains(filter.toLowerCase()) ||
                    created.toLowerCase().contains(filter.toLowerCase())
        }
                .collect(Collectors.toList())
    }

    override fun addTask(name: String, description: String, created: String, photo: String?) {
        tasks.add(Task(counter++, name, description, created, null, photo))
    }

    override fun editTask(id: Int, name: String, description: String, closed: String, photo: String) {
        val task = findTaskByID(id)
        task!!.name = name
        task.desc = description
        task.closed = closed
        task.photo = photo
    }

    override fun closeOrReopenTask(id: Int, closed: String?) {
        findTaskByID(id)?.closed = closed
    }

    override fun deleteTask(id: Int): Int =
            tasks.indexOf(findTaskByID(id)).apply {
                tasks.removeAt(this)
            }


    override fun deleteAllTasks() = tasks.clear()

    override fun findTaskByID(id: Int): Task? =
            tasks.firstOrNull { task -> task.id == id }
}