package ru.job4j.todolist.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.job4j.todolist.Task;

public class SqlStore extends SQLiteOpenHelper implements IStore {
    public static final String DB = "todo.db";
    public static final int VERSION = 1;
    private List<Task> tasks = new ArrayList<>();

    public SqlStore(@Nullable Context context) {
        super(context, DB, null , VERSION);
        Cursor cursor = this.getWritableDatabase().query(
                TodoDbSchema.TodoTable.NAME,
                null,
                null, null,
                null, null, null
        );
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            this.tasks.add(new Task(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("desc")),
                    cursor.getString(cursor.getColumnIndex("created")),
                    cursor.getString(cursor.getColumnIndex("closed"))
            ));
            cursor.moveToNext();
        }
        cursor.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
                db.execSQL(
                "create table " +
                        TodoDbSchema.TodoTable.NAME + " (" +
                        "id integer primary key autoincrement, " +
                        TodoDbSchema.TodoTable.Cols.NAME + ", " +
                        TodoDbSchema.TodoTable.Cols.DESC + ", " +
                        TodoDbSchema.TodoTable.Cols.CREATED + ", " +
                        TodoDbSchema.TodoTable.Cols.CLOSED + " " +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public List<Task> getTasks() {
        return this.tasks;
    }

    @Override
    public List<Task> getFilteredTasks(String filter) {
        return tasks.stream()
                .filter(task -> task.getName().toLowerCase().contains(filter.toLowerCase()) ||
                                task.getDesc().toLowerCase().contains(filter.toLowerCase()) ||
                                task.getCreated().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void addTask(String name,String description,String created) {
        ContentValues value = new ContentValues();
        value.put(TodoDbSchema.TodoTable.Cols.NAME, name);
        value.put(TodoDbSchema.TodoTable.Cols.DESC, description);
        value.put(TodoDbSchema.TodoTable.Cols.CREATED, created);
        int id = (int) this.getWritableDatabase().insert(TodoDbSchema.TodoTable.NAME, null, value);
        Task task = new Task(id, name, description, created, null);
        this.tasks.add(task);
    }

    @Override
    public void editTask(int position, String name, String description, String closed) {
        ContentValues value = new ContentValues();
        value.put(TodoDbSchema.TodoTable.Cols.NAME, name);
        value.put(TodoDbSchema.TodoTable.Cols.DESC, description);
        value.put(TodoDbSchema.TodoTable.Cols.CLOSED, closed);
        Cursor cursor = this.getWritableDatabase().query(
                TodoDbSchema.TodoTable.NAME, null, null, null,
                null, null, null
        );
        cursor.moveToPosition(position);
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String created = cursor.getString(cursor.getColumnIndex("created"));
        cursor.close();
        this.getWritableDatabase().update(TodoDbSchema.TodoTable.NAME,
                value,
                "id = ?",
                new String[]{String.valueOf(id)});
        Task task = new Task(id, name, description, created, closed);
        this.tasks.set(position, task);
    }

    @Override
    public void deleteTask(int position) {
        Cursor cursor = this.getWritableDatabase().query(
                TodoDbSchema.TodoTable.NAME, null, null, null,
                null, null, null
        );
        cursor.moveToPosition(position);
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();
        this.getWritableDatabase().delete(TodoDbSchema.TodoTable.NAME,
                    "id = ?",
                    new String[]{String.valueOf(id)});
//        this.tasks.remove(position);
    }

    @Override
    public void deleteAllTasks() {
        this.getWritableDatabase().delete(TodoDbSchema.TodoTable.NAME, null, null);
        this.tasks.clear();
    }

    @Override
    public Task getTask(int position) {
        return this.tasks.get(position);
    }
}
