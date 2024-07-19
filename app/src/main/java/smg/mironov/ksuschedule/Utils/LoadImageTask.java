package smg.mironov.ksuschedule.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import smg.mironov.ksuschedule.Adapters.TeacherAdapter;
import smg.mironov.ksuschedule.R;

/**
 * Класс для асинхронной загрузки изображения из внутреннего хранилища.
 *
 * @version 1.0
 * @authors
 * Егор Гришанов
 * Александр Миронов
 */
public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private final int teacherId;
    private final ImageView imageView;
    private final TeacherAdapter adapter;

    /**
     * Конструктор для создания задачи загрузки изображения.
     *
     * @param teacherId ID преподавателя
     * @param imageView элемент ImageView для отображения изображения
     * @param adapter   адаптер для обновления данных
     */
    public LoadImageTask(int teacherId, ImageView imageView, TeacherAdapter adapter) {
        this.teacherId = teacherId;
        this.imageView = imageView;
        this.adapter = adapter;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return loadImageFromInternalStorage(teacherId);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    /**
     * Загрузка изображения из внутреннего хранилища по ID пользователя.
     *
     * @param userId ID пользователя
     * @return загруженное изображение в формате {@link Bitmap}, или null если изображение не найдено
     */
    private Bitmap loadImageFromInternalStorage(int userId) {
        try {
            ContextWrapper cw = new ContextWrapper(imageView.getContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory, "profile_" + userId + ".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
