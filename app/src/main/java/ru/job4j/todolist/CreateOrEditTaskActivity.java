package ru.job4j.todolist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import ru.job4j.todolist.store.FileStore;
import ru.job4j.todolist.store.IStore;
import ru.job4j.todolist.store.PhotoFileStore;

public class CreateOrEditTaskActivity extends AppCompatActivity {
    public static final int REQUEST_IMAGE_CAPTURE = 123;
    private IStore store;
    private PhotoFileStore photoStore;
    private int id = -1;
    private EditText mName;
    private EditText mDescription;
    private CheckBox mDone;
    private ImageView mPhoto;
    private String mCurrentPhotoPath;
    private String mOldPhotoPathForDelete;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Picasso.with(this).load(new File(mCurrentPhotoPath)).into(mPhoto);
            if (mOldPhotoPathForDelete != null) {
                photoStore.deleteImageFile(mOldPhotoPathForDelete);
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            photoStore.deleteImageFile(mCurrentPhotoPath);
            mCurrentPhotoPath = mOldPhotoPathForDelete;
        }
        mOldPhotoPathForDelete = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_creat_or_edit_task);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDone = findViewById(R.id.create_edit_task_done);
        mPhoto = findViewById(R.id.create_edit_task_photo);
        mName = findViewById(R.id.create_edit_task_name);
        mDescription = findViewById(R.id.create_edit_task_description);
        this.store = FileStore.getInstance(this);
        this.photoStore = PhotoFileStore.getInstance(this);
        mPhoto.setOnClickListener(this::clickOnPhoto);
        if(getIntent().hasExtra("id for edit")) {
            id = getIntent().getIntExtra("id for edit", -1);
            Task task = store.findTaskByID(id);
            mDone.setVisibility(View.VISIBLE);
            mName.setText(task.getName());
            mName.setHint(task.getName());
            mDescription.setText(task.getDesc());
            mDescription.setHint(task.getDesc());
            mDone.setChecked(task.getClosed() != null);
            mCurrentPhotoPath = task.getPhoto();
            if (mCurrentPhotoPath != null) {
                Picasso.with(this).load(new File(mCurrentPhotoPath)).into(mPhoto);
            }
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
                if(mName.getText().toString().trim().length() < 1) {
                    Toast.makeText(
                            getApplicationContext(), R.string.task_need_name,
                            Toast.LENGTH_SHORT
                    ).show();
                    return true;
                }
                if (id == -1) {
                    createNewTask();
                    setAnswerPositionResult(store.getTasks().size()-1,"add");
                } else {
                    editTask();
                    setAnswerPositionResult(store.getPositionOfTaskById(id),"edit");
                }
                mCurrentPhotoPath = null;
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pathToPhoto", mCurrentPhotoPath);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPhotoPath != null) {
            photoStore.deleteImageFile(mCurrentPhotoPath);
        }
        super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPhotoPath = savedInstanceState.getString("pathToPhoto");
        Picasso.with(this).load(new File(mCurrentPhotoPath)).into(mPhoto);
    }

    private void setAnswerPositionResult(int index, String name) {
        Intent intent = new Intent();
        intent.putExtra(name, index);
        setResult(RESULT_OK, intent);
    }

    private void clickOnPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = photoStore.createImageFile();
            mOldPhotoPathForDelete = mCurrentPhotoPath;
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.provider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void createNewTask() {
        String name = String.valueOf(this.mName.getText());
        String description = String.valueOf(this.mDescription.getText());
        String created = new SimpleDateFormat("dd-MM-yyyy HH:mm E").format(new Date(System.currentTimeMillis()));
        store.addTask(name,description,created,mCurrentPhotoPath);
    }

    private void editTask() {
        String name = String.valueOf(this.mName.getText());
        String description = String.valueOf(this.mDescription.getText());
        String closed = null;
        if (this.mDone.isChecked()) {
            closed = new SimpleDateFormat("dd-MM-yyyy HH:mm E").format(new Date(System.currentTimeMillis()));
        }
        store.editTask(id,name,description,closed,mCurrentPhotoPath);
    }
}
