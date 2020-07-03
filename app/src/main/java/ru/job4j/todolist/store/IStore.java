package ru.job4j.todolist.store;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.Task;

public interface IStore {

    List<Task> getTasks();

    List<Task> getFilteredTasks(String filter);

    void addTask(String name,String description,String created);

    void editTask(int position, String name,String description,String closed);

    void closeOrReopenTask(int position, String closed);

    void deleteTask(int position);

    void deleteAllTasks();

    Task getTask(int position);
}
