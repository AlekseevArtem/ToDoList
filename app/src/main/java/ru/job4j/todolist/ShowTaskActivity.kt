package ru.job4j.todolist

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_show_task.*
import ru.job4j.todolist.store.FileStore
import java.io.File

class ShowTaskActivity : AppCompatActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_show_task)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val position = intent.getIntExtra("id for show", -1)
        val (_, name, desc, created, closed, photo) =
                FileStore.getInstance(this).findTaskByID(position)!!
        show_task_name.text = name
        show_task_description.text = desc
        show_task_created.text = created
        show_task_finished.visibility = if(closed != null) View.VISIBLE else View.INVISIBLE
        show_task_closed.text = closed
        photo?.let {Picasso.with(this).load(File(photo)).into(show_task_photo)}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
