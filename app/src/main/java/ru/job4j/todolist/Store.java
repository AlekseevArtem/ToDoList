package ru.job4j.todolist;

import java.util.ArrayList;
import java.util.List;

class Store {
    private List<Task> tasks = new ArrayList<>();
    private static final Store INST = new Store();

    private Store() {
    }

    public static Store getInstance() {
        return INST;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
