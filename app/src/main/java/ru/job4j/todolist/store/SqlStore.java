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
    private static SqlStore INST;

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

    public static SqlStore getInstance(Context context) {
        if (INST == null){
            INST = new SqlStore(context);
        }
        return INST;
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
    public void editTask(int id, String name, String description, String closed) {
        ContentValues value = new ContentValues();
        value.put(TodoDbSchema.TodoTable.Cols.NAME, name);
        value.put(TodoDbSchema.TodoTable.Cols.DESC, description);
        value.put(TodoDbSchema.TodoTable.Cols.CLOSED, closed);
        this.getWritableDatabase().update(TodoDbSchema.TodoTable.NAME,
                value,
                "id = ?",
                new String[]{String.valueOf(id)});
        Task task = findTaskByID(id);
        task.setName(name);
        task.setDesc(description);
        task.setClosed(closed);
    }

    @Override
    public void closeOrReopenTask(int id, String closed) {
        ContentValues value = new ContentValues();
        value.put(TodoDbSchema.TodoTable.Cols.CLOSED, closed);
        this.getWritableDatabase().update(TodoDbSchema.TodoTable.NAME,
                value,
                "id = ?",
                new String[]{String.valueOf(id)});
        Task task = findTaskByID(id);
        task.setClosed(closed);
    }

    @Override
    public int deleteTask(int id) {
        this.getWritableDatabase().delete(TodoDbSchema.TodoTable.NAME,
                "id = ?",
                new String[]{String.valueOf(id)}
                );
        int result = getPositionOfTaskById(id);
        tasks.remove(result);
        return result;
    }

    @Override
    public void deleteAllTasks() {
        this.getWritableDatabase().delete(TodoDbSchema.TodoTable.NAME, null, null);
        this.tasks.clear();
    }

    @Override
    public Task findTaskByID(int id) {
        return tasks.get(getPositionOfTaskById(id));
    }

    @Override
    public int getPositionOfTaskById(int id) {
        for (int index = 0 ; index < tasks.size(); index ++) {
            if (tasks.get(index).getId() == id) {
                return index;
            }
        }
        return -1;
    }
}
