package ru.job4j.todolist.store

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.job4j.todolist.Task
import java.util.*
import java.util.stream.Collectors

class SqlStore(context: Context) : SQLiteOpenHelper(context, DB, null, VERSION), IStore {
    private val tasks: MutableList<Task> = ArrayList()

    companion object {
        const val DB = "todo.db"
        const val VERSION = 1
        private lateinit var INST: SqlStore
        fun getInstance(context: Context): SqlStore {
            if (!this::INST.isInitialized) {
                INST = SqlStore(context)
                INST.updateStore()
            }
            return INST
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
                "create table " +
                        TodoDbSchema.TodoTable.NAME + " (" +
                        "id integer primary key autoincrement, " +
                        TodoDbSchema.TodoTable.Cols.NAME + ", " +
                        TodoDbSchema.TodoTable.Cols.DESC + ", " +
                        TodoDbSchema.TodoTable.Cols.CREATED + ", " +
                        TodoDbSchema.TodoTable.Cols.CLOSED + ", " +
                        TodoDbSchema.TodoTable.Cols.PHOTO + " " +
                        ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private fun updateStore(): Unit =
            writableDatabase.query(TodoDbSchema.TodoTable.NAME,
                    null,
                    null, null,
                    null, null, null
            ).run {
                moveToFirst()
                while (!isAfterLast) {
                    tasks.add(Task(
                            getInt(getColumnIndex("id")),
                            getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.NAME)),
                            getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.DESC)),
                            getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.CREATED)),
                            getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.CLOSED)),
                            getString(getColumnIndex(TodoDbSchema.TodoTable.Cols.PHOTO))
                    ))
                    moveToNext()
                }
                close()
            }


    override fun getTasks(): List<Task> = tasks

    override fun getFilteredTasks(filter: String): List<Task> = tasks.stream()
            .filter { (_, name, desc, created) ->
                name.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault())) ||
                        desc.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault())) ||
                        created.toLowerCase(Locale.getDefault()).contains(filter.toLowerCase(Locale.getDefault()))
            }
            .collect(Collectors.toList())


    override fun addTask(name: String, description: String, created: String, photo: String?) {
        val value = ContentValues().also {
            it.put(TodoDbSchema.TodoTable.Cols.NAME, name)
            it.put(TodoDbSchema.TodoTable.Cols.NAME, name)
            it.put(TodoDbSchema.TodoTable.Cols.DESC, description)
            it.put(TodoDbSchema.TodoTable.Cols.PHOTO, photo)
        }
        val id = writableDatabase.insert(TodoDbSchema.TodoTable.NAME, null, value).toInt()
        Task(id, name, description, created, null, photo).let { tasks.add(it) }
    }

    override fun editTask(id: Int, name: String, description: String, closed: String, photo: String) {
        val value = ContentValues().also {
            it.put(TodoDbSchema.TodoTable.Cols.NAME, name)
            it.put(TodoDbSchema.TodoTable.Cols.NAME, name)
            it.put(TodoDbSchema.TodoTable.Cols.DESC, description)
            it.put(TodoDbSchema.TodoTable.Cols.CLOSED, closed)
            it.put(TodoDbSchema.TodoTable.Cols.PHOTO, photo)
        }
        writableDatabase.update(TodoDbSchema.TodoTable.NAME, value, "id = ?", arrayOf(id.toString()))
        findTaskByID(id)?.also {
            it.name = name
            it.desc = name
            it.closed = name
            it.photo = name
        }
    }

    override fun closeOrReopenTask(id: Int, closed: String?) {
        val value = ContentValues().also {
            it.put(TodoDbSchema.TodoTable.Cols.CLOSED, closed)
        }
        writableDatabase.update(TodoDbSchema.TodoTable.NAME, value, "id = ?", arrayOf(id.toString()))
        findTaskByID(id)?.closed = closed
    }

    override fun deleteTask(id: Int): Int =
            tasks.indexOf(findTaskByID(id)).apply {
                writableDatabase.delete(TodoDbSchema.TodoTable.NAME, "id = ?", arrayOf(id.toString()))
                tasks.removeAt(this)
            }

    override fun deleteAllTasks() {
        writableDatabase.delete(TodoDbSchema.TodoTable.NAME, null, null)
        tasks.clear()
    }

    override fun findTaskByID(id: Int): Task? = tasks.firstOrNull { task -> task.id == id }
}