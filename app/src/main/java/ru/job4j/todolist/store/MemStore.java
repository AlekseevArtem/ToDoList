package ru.job4j.todolist.store;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.job4j.todolist.Task;

public class MemStore implements IStore {
    private List<Task> tasks = new ArrayList<>();
    private static final MemStore INST = new MemStore();
    private int counter = 0;

    private MemStore() {
    }

    public MemStore(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static MemStore getInstance(Context context) {
        return INST;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    @Override
    public List<Task> getFilteredTasks(String filter) {
        return this.tasks.stream().filter(
                task -> task.getName().toLowerCase().contains(filter.toLowerCase()) ||
                        task.getDesc().toLowerCase().contains(filter.toLowerCase()) ||
                        task.getCreated().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void addTask(String name,String description,String created, String photo) {
        this.tasks.add(new Task(counter++, name, description, created,null, photo));
    }

    @Override
    public void editTask(int id, String name,String description,String closed, String photo) {
        Task task = findTaskByID(id);
        task.setName(name);
        task.setDesc(description);
        task.setClosed(closed);
        task.setPhoto(photo);
    }

    @Override
    public void closeOrReopenTask(int id, String closed) {
        Task task = findTaskByID(id);
        task.setClosed(closed);
    }

    @Override
    public int deleteTask(int id) {
        int result = getPositionOfTaskById(id);
        this.tasks.remove(result);
        return result;
    }

    @Override
    public void deleteAllTasks() {
        this.tasks.clear();
    }

    @Override
    public Task findTaskByID(int id) {
        return this.tasks.get(getPositionOfTaskById(id));
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
