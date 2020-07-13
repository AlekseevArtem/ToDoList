package ru.job4j.todolist.store;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.job4j.todolist.Task;

public class FileStore implements IStore{
    private static FileStore INST;
    private int counter = 0;
    private Context context;
    private List<Task> tasks = new ArrayList<>();

    private FileStore(Context context) {
        this.context = context;
        File fileDir = new File(String.valueOf(context.getFilesDir()));
        File[] allFiles = fileDir.listFiles();
        for (int index = 0; index < Objects.requireNonNull(allFiles).length; index++) {
            try (BufferedReader in = new BufferedReader(new FileReader(allFiles[index]))) {
                Task task = new Task(Integer.parseInt(in.readLine()), in.readLine(), in.readLine(),in.readLine(),in.readLine());
                tasks.add(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (allFiles.length != 0) counter = Integer.parseInt(allFiles[allFiles.length-1].getName().split("\\.")[0]) + 1;
    }

    public static FileStore getInstance(Context context) {
        if (INST == null) {
            INST = new FileStore(context);
        }
        return INST;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
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
    public void addTask(String name, String description, String created) {
        File file = new File(context.getFilesDir(), (counter) + ".txt");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(counter);
            out.println(name);
            out.println(description);
            out.println(created);
            tasks.add(new Task(counter++, name, description, created, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editTask(int id, String name, String description, String closed) {
        File file = new File(context.getFilesDir(), id + ".txt");
        Task task = findTaskByID(id);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(task.getId());
            out.println(name);
            out.println(description);
            out.println(task.getCreated());
            out.println(closed);
            task.setName(name);
            task.setDesc(description);
            task.setClosed(closed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeOrReopenTask(int id, String closed) {
        File file = new File(context.getFilesDir(), id + ".txt");
        Task task = findTaskByID(id);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            out.println(task.getId());
            out.println(task.getName());
            out.println(task.getDesc());
            out.println(task.getCreated());
            out.println(closed);
            task.setClosed(closed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int deleteTask(int id) {
        File file = new File(context.getFilesDir(), id + ".txt");
        file.delete();
        Task task = findTaskByID(id);
        int result = tasks.indexOf(task);
        tasks.remove(result);
        return result;
    }

    @Override
    public void deleteAllTasks() {
        File fileDir = new File(String.valueOf(context.getFilesDir()));
        File[] allFiles = fileDir.listFiles();
        for (File file : Objects.requireNonNull(allFiles)) {
            file.delete();
        }
        tasks.clear();
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
