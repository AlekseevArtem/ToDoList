package ru.job4j.todolist.store;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import ru.job4j.todolist.Task;

public interface IStore {
    List<Task> tasks = new ArrayList<>();

    public List<Task> getTasks();

    public List<Task> getFilteredTasks(String filter);

    public void addTask(String name,String description,String created);

    public void editTask(int position, String name,String description,String closed);

    public void deleteTask(int position);

    public void deleteAllTasks();

    public Task getTask(int position);
}
