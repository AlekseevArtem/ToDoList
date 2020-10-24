package ru.job4j.todolist.contentProvider

import android.database.AbstractCursor
import ru.job4j.todolist.Task

class StoreCursor(private val store: List<Task>) : AbstractCursor() {

    override fun getCount(): Int = store.size

    override fun getColumnNames(): Array<String> = arrayOf("_ID", "NAME")

    override fun getString(column: Int): String? =
            if(column == 1) store[position].name else null

    override fun getShort(column: Int): Short = 0

    override fun getInt(column: Int): Int = 0

    override fun getLong(column: Int): Long = 0

    override fun getFloat(column: Int): Float = 0F

    override fun getDouble(column: Int): Double = 0.0

    override fun isNull(column: Int): Boolean = false
}