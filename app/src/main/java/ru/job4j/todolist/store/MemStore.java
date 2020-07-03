package ru.job4j.todolist.store;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ru.job4j.todolist.Task;

public class MemStore implements IStore {
    private List<Task> tasks = new ArrayList<>();
    private static final MemStore INST = new MemStore();

    private MemStore() {
    }

    public MemStore(List<Task> tasks) {
        this.tasks = tasks;
    }

    public static MemStore getInstance() {
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
    public void addTask(String name,String description,String created) {
        this.tasks.add(new Task(name,description,created));
    }

    @Override
    public void editTask(int position, String name,String description,String closed) {
        Task task = this.tasks.get(position);
        task.setName(name);
        task.setDesc(description);
        task.setClosed(closed);
    }

    @Override
    public void closeOrReopenTask(int position, String closed) {
        Task task = this.tasks.get(position);
        task.setClosed(closed);
    }

    @Override
    public void deleteTask(int position) {
        this.tasks.remove(position);
    }

    @Override
    public void deleteAllTasks() {
        this.tasks.clear();
    }

    @Override
    public Task getTask(int position) {
        return this.tasks.get(position);
    }
}
