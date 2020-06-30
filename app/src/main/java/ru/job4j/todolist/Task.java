package ru.job4j.todolist;

public class Task {
    private int id;
    private String name;
    private String desc;
    private String created;
    private String closed;

    public Task(int id, String name, String desc, String created, String closed) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.closed = closed;
    }

    public Task(String name, String desc, String created) {
        this.name = name;
        this.desc = desc;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
