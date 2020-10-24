package ru.job4j.todolist

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_creat_or_edit_task.*
import ru.job4j.todolist.store.FileStore
import ru.job4j.todolist.store.IStore
import ru.job4j.todolist.store.PhotoFileStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateOrEditTaskActivity : AppCompatActivity() {
    private var store: IStore? = null
    private var photoStore: PhotoFileStore? = null
    private var id = -1
    private var mCurrentPhotoPath: String? = null
    private var mOldPhotoPathForDelete: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Picasso.with(this).load(File(mCurrentPhotoPath)).into(create_edit_task_photo)
            if (mOldPhotoPathForDelete != null) {
                photoStore!!.deleteImageFile(mOldPhotoPathForDelete!!)
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            photoStore!!.deleteImageFile(mCurrentPhotoPath!!)
            mCurrentPhotoPath = mOldPhotoPathForDelete
        }
        mOldPhotoPathForDelete = null
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_creat_or_edit_task)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        store = FileStore.getInstance(this)
        photoStore = PhotoFileStore.getInstance(this)
        create_edit_task_photo.setOnClickListener { clickOnPhoto() }
        if (intent.hasExtra("id for edit")) {
            id = intent.getIntExtra("id for edit", -1)
            val (_, name, desc, _, closed, photo) = (store as FileStore).findTaskByID(id)!!
            create_edit_task_done.visibility = View.VISIBLE
            create_edit_task_name.setText(name)
            create_edit_task_name.hint = name
            create_edit_task_description.setText(desc)
            create_edit_task_description.hint = desc
            create_edit_task_done.isChecked = closed != null
            mCurrentPhotoPath = photo
            if (mCurrentPhotoPath != null) {
                Picasso.with(this).load(File(mCurrentPhotoPath)).into(create_edit_task_photo)
            }
        }
    }

    override fun onCreatePanelMenu(featureId: Int, menu: Menu): Boolean {
        return super.onCreatePanelMenu(featureId, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_creat_or_edit_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_edit_confirm -> {
                if (create_edit_task_name.text.toString().trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(applicationContext, R.string.task_need_name, Toast.LENGTH_SHORT).show()
                    return true
                }
                if (id == -1) {
                    createNewTask()
                    setAnswerPositionResult(store!!.getTasks().size - 1, "add")
                } else {
                    editTask()
                    setAnswerPositionResult(store?.getTasks()?.indexOfFirst { task -> task.id == id }!!, "edit")
                }
                mCurrentPhotoPath = null
                onBackPressed()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("pathToPhoto", mCurrentPhotoPath)
    }

    override fun onBackPressed() {
        if (mCurrentPhotoPath != null) {
            photoStore!!.deleteImageFile(mCurrentPhotoPath!!)
        }
        super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString("pathToPhoto")?.let {
            Picasso.with(this).load(File(it)).into(create_edit_task_photo)
        }
    }

    private fun setAnswerPositionResult(index: Int, name: String) {
        setResult(RESULT_OK, Intent().putExtra(name, index))
    }

    private fun clickOnPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = photoStore!!.createImageFile()
            mOldPhotoPathForDelete = mCurrentPhotoPath
            mCurrentPhotoPath = photoFile?.absolutePath
            val photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.provider",
                    photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createNewTask() {
        val name = create_edit_task_name.text.toString()
        val description = create_edit_task_description.text.toString()
        val created = SimpleDateFormat("dd-MM-yyyy HH:mm E").format(Date(System.currentTimeMillis()))
        store?.addTask(name, description, created, mCurrentPhotoPath)
    }

    private fun editTask() {
        val name = create_edit_task_name.text.toString()
        val description = create_edit_task_description.text.toString()
        var closed: String? = null
        if (create_edit_task_done.isChecked) {
            closed = SimpleDateFormat("dd-MM-yyyy HH:mm E").format(Date(System.currentTimeMillis()))
        }
        store!!.editTask(id, name, description, closed!!, mCurrentPhotoPath!!)
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 123
    }
}