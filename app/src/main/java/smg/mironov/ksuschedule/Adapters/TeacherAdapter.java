package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.R;
import smg.mironov.ksuschedule.TeachersActivity;
import smg.mironov.ksuschedule.Utils.LoadImageTask;

/**
 * Адаптер для отображения списка преподавателей.
 *
 * @version 1.0
 * @author
 * Егор Гришанов
 * Александр Миронов
 */
public class TeacherAdapter extends ArrayAdapter<TeacherDto> {

    private OnItemClickListener clickListener;
    private String token;

    /**
     * Конструктор для создания объекта {@link TeacherAdapter}.
     *
     * @param context  контекст
     * @param teachers список преподавателей
     * @param listener обработчик кликов на элементах списка
     * @param token    токен для аутентификации
     */
    public TeacherAdapter(Context context, List<TeacherDto> teachers, OnItemClickListener listener, String token) {
        super(context, 0, teachers);
        this.clickListener = listener;
        this.token = token;
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView positionTextView;
        ImageView profileImageView;
        ImageView getInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_info_teacher, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.NameTeacher);
            viewHolder.positionTextView = convertView.findViewById(R.id.PostTeacher);
            viewHolder.profileImageView = convertView.findViewById(R.id.teacherImage);
            viewHolder.getInfo = convertView.findViewById(R.id.blue_arrow_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TeacherDto currentTeacher = getItem(position);

        if (currentTeacher != null) {
            viewHolder.nameTextView.setText(currentTeacher.getName());
            viewHolder.positionTextView.setText(currentTeacher.getPost());

            // Перед загрузкой изображения проверяем, не уничтожена ли активность
            if (getContext() instanceof TeachersActivity && !((TeachersActivity) getContext()).isDestroyed()) {
                // Загрузка изображения профиля
                new LoadImageTask(currentTeacher.getId(), viewHolder.profileImageView, this).execute();
            }

            // Обработка клика на элемент списка
            viewHolder.getInfo.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(currentTeacher.getId(), viewHolder.profileImageView);
                }
            });
        }

        return convertView;
    }


    /**
     * Обновляет фото преподавателя в списке.
     *
     * @param teacherId идентификатор преподавателя
     * @param bitmap    изображение преподавателя
     */
    public void updateTeacherPhoto(Long teacherId, Bitmap bitmap) {
        for (int i = 0; i < getCount(); i++) {
            TeacherDto teacher = getItem(i);
            if (teacher != null && teacher.getId() == teacherId) {
                View listItemView = getView(i, null, null);
                ImageView profileImageView = listItemView.findViewById(R.id.teacherImage);
                profileImageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Интерфейс для обработки кликов на элементах списка.
     */
    public interface OnItemClickListener {
        void onItemClick(int teacherId, ImageView infoTeacher);
    }
}
