package ru.job4j.todolist.contentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import ru.job4j.todolist.store.SqlStore

class StoreContentProvider : ContentProvider() {
    private var store: SqlStore? = null

    companion object {
        val CONTENT_URI = Uri.parse("content://ru.job4j.todo/items")
    }

    override fun onCreate(): Boolean {
        store = SqlStore(context!!)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? =
        StoreCursor(store!!.getFilteredTasks(selection!!))

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0
}