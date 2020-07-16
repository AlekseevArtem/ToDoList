package ru.job4j.todolist.store;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.job4j.todolist.Task;

public class PhotoFileStore {
    private static PhotoFileStore INST;
    private Context context;

    private PhotoFileStore(Context context) {
        this.context = context;
    }

    public static PhotoFileStore getInstance(Context context) {
        if (INST == null) {
            INST = new PhotoFileStore(context);
        }
        return INST;
    }

    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File result = null;
        try {
            result = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void deleteAllImageFile() {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] allFiles = Objects.requireNonNull(storageDir).listFiles();
        for (File file : Objects.requireNonNull(allFiles)) {
            file.delete();
        }
    }

    public void deleteImageFile(String path) {
        File image = new File(path);
        image.delete();
    }
}
