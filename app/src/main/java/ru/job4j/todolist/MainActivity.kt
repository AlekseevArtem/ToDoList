package ru.job4j.todolist

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.rv_container_for_task.view.*
import ru.job4j.todolist.ConfirmDeleteDialogFragment.ConfirmDeleteDialogListener
import ru.job4j.todolist.store.FileStore
import ru.job4j.todolist.store.IStore
import ru.job4j.todolist.store.PhotoFileStore
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), ConfirmDeleteDialogListener {
    private lateinit var recycler: RecyclerView
    private lateinit var photoStore: PhotoFileStore
    private lateinit var store: IStore
    companion object { const val CHANGED_TASK = 0 }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        if (data?.hasExtra("add") == true) {
            recycler.adapter?.notifyItemInserted(data.getIntExtra("add", -1))
        } else if (data!!.hasExtra("edit")) {
            recycler.adapter?.notifyItemChanged(data.getIntExtra("edit", -1))
        }
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContentView(R.layout.activity_main)
        recycler = activity_main
        recycler.layoutManager = LinearLayoutManager(this)
        store = FileStore.getInstance(this)
        photoStore = PhotoFileStore.getInstance(this)
        updateUI()
    }

    private fun updateUI() {
        recycler.adapter = TasksAdapter(store.getTasks())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_icon -> {
                val searchView = item.actionView as SearchView
                searchView.queryHint = "Search here"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        recycler.adapter = TasksAdapter(store.getFilteredTasks(newText))
                        return true
                    }
                })
                true
            }
            R.id.create_new_task -> {
                addOne()
                true
            }
            R.id.delete_tasks -> {
                val dialog: DialogFragment = ConfirmDeleteDialogFragment(R.string.dialog_delete_all)
                dialog.show(Objects.requireNonNull(supportFragmentManager), "dialog_delete_tag")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPositiveDialogClick(dialog: DialogFragment) = deleteAll()

    override fun onNegativeDialogClick(dialog: DialogFragment) {}

    private fun deleteAll() {
        store.deleteAllTasks()
        photoStore.deleteAllImageFile()
        updateUI()
    }

    private fun addOne() {
        val intent = Intent(applicationContext, CreateOrEditTaskActivity::class.java)
        startActivityForResult(intent, CHANGED_TASK)
    }

    inner class TasksHolder(inflater: LayoutInflater, parent: ViewGroup?) :
            RecyclerView.ViewHolder(inflater.inflate(R.layout.rv_container_for_task, parent, false)) {

        fun bind(task: Task, position: Int) {
            val status = if (task.closed == null) R.string.task_status_not_done else R.string.task_status_done
            itemView.apply {
                rv_task_name.text = task.name
                setOnClickListener { callShowActivity(task.id!!) }
                rv_task_status.setText(status)
                rv_task_edit.setOnClickListener { callEditActivity(task.id!!) }
                rv_task_delete.setOnClickListener { deleteTask(task.id!!) }
                rv_task_check_done.isChecked = task.closed != null
                rv_task_check_done.setOnClickListener { view: View -> makeTaskDoneOrUndone(view, task.id!!, position) }
            }
        }

        private fun callShowActivity(id: Int) {
            val intent = Intent( itemView.context, ShowTaskActivity::class.java)
            intent.putExtra("id for show", id)
            itemView.context.startActivity(intent)
        }

        private fun callEditActivity(id: Int) {
            val intent = Intent(this@MainActivity, CreateOrEditTaskActivity::class.java)
            intent.putExtra("id for edit", id)
            startActivityForResult(intent, CHANGED_TASK)
        }

        private fun deleteTask(id: Int) {
            store.findTaskByID(id)?.photo?.let { photoStore.deleteImageFile(it) }
            recycler.adapter?.notifyItemRemoved(store.deleteTask(id))
        }

        private fun makeTaskDoneOrUndone(view: View, id: Int, position: Int) {
            val doneUndone = view.findViewById<CheckBox>(R.id.rv_task_check_done)
            if (!doneUndone.isChecked) {
                doneUndone.isChecked = false
                store.closeOrReopenTask(id, null)
            } else {
                doneUndone.isChecked = true
                store.closeOrReopenTask(id,
                        SimpleDateFormat("dd-MM-yyyy HH:mm E").format(Date(System.currentTimeMillis())))
            }
            recycler.adapter?.notifyItemChanged(position)
        }
    }

    inner class TasksAdapter(private val tasks: List<Task>) : RecyclerView.Adapter<TasksHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksHolder =
                TasksHolder(LayoutInflater.from(parent.context), parent)

        override fun onBindViewHolder(holder: TasksHolder, position: Int) =
                holder.bind(tasks[position], position)

        override fun getItemCount(): Int = tasks.size
    }
}