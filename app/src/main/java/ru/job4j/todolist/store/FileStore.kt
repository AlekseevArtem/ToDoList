package ru.job4j.todolist.store

import android.content.Context
import ru.job4j.todolist.Task
import java.io.*
import java.util.*
import java.util.stream.Collectors

class FileStore private constructor(private val context: Context) : IStore {
    private var counter = 0
    private val tasks: MutableList<Task> = ArrayList()

    private fun updateStore() {
        val allFiles = File(context.filesDir.toString()).listFiles()
        allFiles?.forEach {
            BufferedReader(FileReader(it)).use { `in` ->
                val task = Task(`in`.readLine().toInt(), `in`.readLine(),
                        `in`.readLine(), `in`.readLine(), `in`.readLine(), `in`.readLine())
                if (task.closed == "") task.closed = null
                tasks.add(task)
            }
        }
        if (allFiles != null && allFiles.isNotEmpty()) {
            counter = allFiles[allFiles.size - 1].name.split("\\.".toRegex()).toTypedArray()[0].toInt() + 1
        }
    }

    override fun getTasks(): List<Task> = tasks

    override fun getFilteredTasks(filter: String): List<Task> =
            tasks.stream().filter { (_, name, desc, created) ->
                name.toLowerCase().contains(filter.toLowerCase()) ||
                        desc.toLowerCase().contains(filter.toLowerCase()) ||
                        created.toLowerCase().contains(filter.toLowerCase())
            }.collect(Collectors.toList())

    override fun addTask(name: String, description: String, created: String, photo: String?) {
        val file = File(context.filesDir, "$counter.txt")
        PrintWriter(BufferedWriter(FileWriter(file))).use { out ->
            out.println(counter)
            out.println(name)
            out.println(description)
            out.println(created)
            out.println("")
            out.println(photo)
            tasks.add(Task(counter++, name, description, created, null, photo))
        }
    }

    override fun editTask(id: Int, name: String, description: String, closed: String, photo: String) {
        val file = File(context.filesDir, "$id.txt")
        findTaskByID(id)?.apply {
            PrintWriter(BufferedWriter(FileWriter(file))).use { out ->
                out.println(this.id)
                out.println(name)
                out.println(description)
                out.println(created)
                out.println(closed)
                out.println(photo)
                this.name = name
                this.desc = description
                this.closed = closed
                this.photo = photo
            }
        }

    }

    override fun closeOrReopenTask(id: Int, closed: String?) {
        val file = File(context.filesDir, "$id.txt")
        findTaskByID(id)?.apply {
            PrintWriter(BufferedWriter(FileWriter(file))).use { out ->
                out.println(this.id)
                out.println(name)
                out.println(desc)
                out.println(created)
                out.println(closed)
                out.println(photo)
                this.closed = closed
            }
        }
    }

    override fun deleteTask(id: Int): Int =
            tasks.indexOf(findTaskByID(id)).apply {
                File(context.filesDir, "$id.txt").delete()
                tasks.removeAt(this)
            }


    override fun deleteAllTasks(): Unit =
            File(context.filesDir.toString()).listFiles()?.let {
                it.forEach { file -> file.delete() }
                tasks.clear()
            }!!

    override fun findTaskByID(id: Int): Task? = tasks.firstOrNull { task -> task.id == id }

    companion object {
        private lateinit var INST: FileStore
        fun getInstance(context: Context): FileStore {
            if (!this::INST.isInitialized) {
                INST = FileStore(context)
                INST.updateStore()
            }
            return INST
        }
    }
}