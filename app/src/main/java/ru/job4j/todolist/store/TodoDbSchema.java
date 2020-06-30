package ru.job4j.todolist.store;

public class TodoDbSchema {
    public static final class TodoTable {
        public static final String NAME = "todo";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String DESC = "desc";
            public static final String CREATED = "created";
            public static final String CLOSED = "closed";
        }
    }
}