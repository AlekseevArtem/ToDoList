package ru.job4j.todolist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.job4j.todolist.store.IStore;
import ru.job4j.todolist.store.SqlStore;

public class MainActivity extends AppCompatActivity implements ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener {
    private RecyclerView recycler;
    private IStore store;
    private final int CHANGED_TASK = 0;

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (Objects.requireNonNull(data).hasExtra("add")) {
            Objects.requireNonNull(recycler.getAdapter()).
                    notifyItemInserted(data.getIntExtra("add", -1));
        } else if (data.hasExtra("edit")) {
            Objects.requireNonNull(recycler.getAdapter()).
                    notifyItemChanged(data.getIntExtra("edit", -1));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        this.recycler = findViewById(R.id.activity_main);
        this.recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.store = SqlStore.getInstance(this);
        updateUI();
    }

    private void updateUI() {
        this.recycler.setAdapter(new TasksAdapter(store.getTasks()));
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_icon:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("Search here");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        recycler.setAdapter(new TasksAdapter(store.getFilteredTasks(newText)));
                        return true;
                    }
                });
                return true;
            case R.id.create_new_task:
                addOne();
                return true;
            case R.id.delete_tasks:
                DialogFragment dialog = new ConfirmDeleteDialogFragment(R.string.dialog_delete_all);
                dialog.show(Objects.requireNonNull(getSupportFragmentManager()), "dialog_delete_tag");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPositiveDialogClick(DialogFragment dialog) {
        deleteAll();
    }

    @Override
    public void onNegativeDialogClick(DialogFragment dialog) {
    }

    private void deleteAll() {
        store.deleteAllTasks();
        updateUI();
    }

    private void addOne() {
        Intent intent = new Intent(getApplicationContext(), CreateOrEditTaskActivity.class);
        startActivityForResult(intent,CHANGED_TASK);
    }

    public class TasksHolder extends RecyclerView.ViewHolder {
        private View view;

        public TasksHolder(@NonNull View view) {
            super(view);
            this.view = itemView;
        }
    }

    public class TasksAdapter extends RecyclerView.Adapter<TasksHolder> {
        private final List<Task> tasks;
        public TasksAdapter(List<Task> tasks) {
            this.tasks = tasks;
        }
        @NonNull
        @Override
        public TasksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.rv_container_for_task, parent, false);
            return new TasksHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TasksHolder holder, int i) {
            final Task task = this.tasks.get(i);
            int status = task.getClosed() == null ? R.string.task_status_not_done : R.string.task_status_done;
            TextView taskName = holder.view.findViewById(R.id.rv_task_name);
            taskName.setText(task.getName());
            taskName.setOnClickListener(view -> callShowActivity(view, i));
            ((TextView) holder.view.findViewById(R.id.rv_task_status)).setText(status);
            ((ImageView) holder.view.findViewById(R.id.rv_task_edit)).setOnClickListener(view -> callEditActivity(view, i));
            ((ImageView) holder.view.findViewById(R.id.rv_task_delete)).setOnClickListener(view -> deleteTask(view, i));
            CheckBox doneUndone = holder.view.findViewById(R.id.rv_task_check_done);
            doneUndone.setChecked(task.getClosed() != null);
            doneUndone.setOnClickListener(view -> makeTaskDoneOrUndone(view, i));
        }

        private void callShowActivity(View view, int position) {
            Intent intent = new Intent(getApplicationContext(), ShowTaskActivity.class);
            intent.putExtra("position for show", position);
            startActivity(intent);
        }

        private void callEditActivity(View view, int position) {
            Intent intent = new Intent(getApplicationContext(), CreateOrEditTaskActivity.class);
            intent.putExtra("position for edit", position);
            startActivityForResult(intent,CHANGED_TASK);
        }

        private void deleteTask(View view, int index) {
            tasks.remove(index);
            store.deleteTask(index);
            notifyItemRemoved(index);
        }

        private void makeTaskDoneOrUndone(View view, int index) {
            CheckBox doneUndone = view.findViewById(R.id.rv_task_check_done);
            if (!doneUndone.isChecked()) {
                doneUndone.setChecked(false);
                store.closeOrReopenTask(index, null);
            } else {
                doneUndone.setChecked(true);
                store.closeOrReopenTask(index,
                        new SimpleDateFormat("dd-MM-yyyy HH:mm E").format(new Date(System.currentTimeMillis())));
            }
            notifyItemChanged(index);
        }

        @Override
        public int getItemCount() {
            return this.tasks.size();
        }
    }

}