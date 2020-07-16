package ru.job4j.todolist;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;

import ru.job4j.todolist.store.FileStore;
import ru.job4j.todolist.store.IStore;

public class ShowTaskActivity extends AppCompatActivity {
    private IStore store;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_show_task);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        int position = getIntent().getIntExtra("id for show", -1);
        this.store = FileStore.getInstance(this);
        Task task = store.findTaskByID(position);
        ((TextView) findViewById(R.id.show_task_name)).setText(task.getName());
        ((TextView) findViewById(R.id.show_task_description)).setText(task.getDesc());
        ((TextView) findViewById(R.id.show_task_created))
                .setText(task.getCreated());
        if (task.getClosed() != null){
            ((TextView) findViewById(R.id.show_task_finished))
                    .setVisibility(View.VISIBLE);
        }
        ((TextView) findViewById(R.id.show_task_closed))
                .setText(task.getClosed());
        ImageView photo = findViewById(R.id.show_task_photo);
        if(task.getPhoto() != null) {
            Picasso.with(this).load(new File(task.getPhoto())).into(photo);
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
