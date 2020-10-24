package ru.job4j.todolist.store

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoFileStore private constructor(private val context: Context) {
    companion object {
        private lateinit var INST: PhotoFileStore
        fun getInstance(context: Context): PhotoFileStore {
            if (!this::INST.isInitialized) INST = PhotoFileStore(context)
            return INST
        }
    }

    fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun deleteAllImageFile() {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        storageDir?.listFiles()?.forEach { file -> file.delete()}
    }

    fun deleteImageFile(path: String) {
        File(path).delete()
    }
}