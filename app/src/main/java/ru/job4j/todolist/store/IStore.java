package ru.job4j.todolist.store;

import java.util.List;

import ru.job4j.todolist.Task;

public interface IStore {

    List<Task> getTasks();

    List<Task> getFilteredTasks(String filter);

    void addTask(String name,String description,String created, String photo);

    void editTask(int id, String name,String description,String closed, String photo);

    void closeOrReopenTask(int id, String closed);

    int deleteTask(int id);

    void deleteAllTasks();

    Task findTaskByID(int id);

    int getPositionOfTaskById(int id);
}
