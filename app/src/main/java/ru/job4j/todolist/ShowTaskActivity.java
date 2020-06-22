package ru.job4j.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowTaskActivity extends AppCompatActivity {
    private Store store = Store.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_show_task);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        int index = getIntent().getIntExtra("index", -1);
        Task task = store.getTasks().get(index);
        ((TextView) findViewById(R.id.show_task_name)).setText(task.getName());
        ((TextView) findViewById(R.id.show_task_description)).setText(task.getDesc());
        ((TextView) findViewById(R.id.show_task_created)).setText(task.getCreated());
        if (task.getClosed() != null){
            ((TextView) findViewById(R.id.show_task_finished)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.show_task_closed)).setText(task.getClosed());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
