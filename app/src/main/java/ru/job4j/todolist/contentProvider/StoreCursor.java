package ru.job4j.todolist.contentProvider;

import android.database.AbstractCursor;

import java.util.List;

import ru.job4j.todolist.Task;

public class StoreCursor extends AbstractCursor {
    private final List<Task> store;

    public StoreCursor(List<Task> store) {
        this.store = store;
    }

    @Override
    public int getCount() {
        return store.size();
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"_ID", "NAME"};
    }

    @Override
    public String getString(int column) {
        Task task = store.get(getPosition());
        String value = null;
        if (column == 1) {
            value = task.getName();
        }
        return value;
    }

    @Override
    public short getShort(int column) {
        return 0;
    }

    @Override
    public int getInt(int column) {
        return 0;
    }

    @Override
    public long getLong(int column) {
        return 0;
    }

    @Override
    public float getFloat(int column) {
        return 0;
    }

    @Override
    public double getDouble(int column) {
        return 0;
    }

    @Override
    public boolean isNull(int column) {
        return false;
    }
}