package ru.job4j.todolist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ru.job4j.todolist.store.IStore;
import ru.job4j.todolist.store.SqlStore;
import ru.job4j.todolist.store.TodoDbSchema;

public class CreateOrEditTaskActivity extends AppCompatActivity {
    private IStore store;
    private int position = -1;

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_creat_or_edit_task);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        CheckBox done = findViewById(R.id.create_edit_task_done);
        this.store = new SqlStore(this);
        if(getIntent().hasExtra("position for edit")) {
            position = getIntent().getIntExtra("position for edit", -1);
            Task task = store.getTask(position);
            done.setVisibility(View.VISIBLE);
            EditText name = findViewById(R.id.create_edit_task_name);
            name.setText(task.getName());
            name.setHint(task.getName());
            EditText description = findViewById(R.id.create_edit_task_description);
            description.setText(task.getDesc());
            description.setHint(task.getDesc());
            boolean finished = task.getClosed() != null;
            ((CheckBox) findViewById(R.id.create_edit_task_done)).setChecked(finished);

        }
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_creat_or_edit_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_edit_confirm:
                if(((EditText) findViewById(R.id.create_edit_task_name)).getText().toString().trim().length() < 1) {
                    Toast.makeText(
                            getApplicationContext(), R.string.task_need_name,
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                }
                if (position == -1) {
                    createNewTask();
                    setAnswerPositionResult(store.getTasks().size()-1,"add");
                } else {
                    editTask();
                    setAnswerPositionResult(position,"edit");
                }
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAnswerPositionResult(int index, String name) {
        Intent intent = new Intent();
        intent.putExtra(name, index);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void createNewTask() {
        String name = String.valueOf(((EditText) findViewById(R.id.create_edit_task_name)).getText());
        String description = String.valueOf(((EditText) findViewById(R.id.create_edit_task_description)).getText());
        String created = new SimpleDateFormat("dd-MM-yyyy HH:mm E").format(new Date(System.currentTimeMillis()));
        store.addTask(name,description,created);
    }

    private void editTask() {
        String name = String.valueOf(((EditText) findViewById(R.id.create_edit_task_name)).getText());
        String description = String.valueOf(((EditText) findViewById(R.id.create_edit_task_description)).getText());
        String closed = null;
        if (((CheckBox) findViewById(R.id.create_edit_task_done)).isChecked()) {
            closed = new SimpleDateFormat("dd-MM-yyyy HH:mm E").format(new Date(System.currentTimeMillis()));
        }
        store.editTask(position,name,description,closed);

    }
}
