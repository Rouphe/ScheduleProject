package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
// Импортируйте необходимые классы для фильтрации
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.R;
import smg.mironov.ksuschedule.Utils.LoadImageTask;

public class TeacherAdapter extends ArrayAdapter<TeacherDto> implements Filterable {

    private OnItemClickListener clickListener;
    private String token;
    private List<TeacherDto> originalList; // Оригинальный список данных
    private List<TeacherDto> filteredList; // Отфильтрованный список данных
    private TeacherFilter filter;

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
        this.originalList = new ArrayList<>(teachers);
        this.filteredList = new ArrayList<>(teachers);
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public TeacherDto getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new TeacherFilter();
        }
        return filter;
    }

    // Метод для отображения сообщения при отсутствии результатов
    public boolean isEmpty() {
        return filteredList.isEmpty();
    }

    private class TeacherFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<TeacherDto> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filtered.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TeacherDto teacher : originalList){
                    // Фильтрация по имени и должности
                    if (teacher.getName().toLowerCase().contains(filterPattern) ||
                            teacher.getPost().toLowerCase().contains(filterPattern)){
                        filtered.add(teacher);
                    }
                }
            }

            results.values = filtered;
            results.count = filtered.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<TeacherDto>) results.values);
            notifyDataSetChanged();
        }
    }

    // Обновление данных адаптера
    public void updateData(List<TeacherDto> teachers){
        originalList.clear();
        originalList.addAll(teachers);
        filteredList.clear();
        filteredList.addAll(teachers);
        notifyDataSetChanged();
    }

    // Остальные методы адаптера остаются без изменений

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
            if (getContext() instanceof smg.mironov.ksuschedule.TeachersActivity &&
                    !((smg.mironov.ksuschedule.TeachersActivity) getContext()).isDestroyed()) {
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
     * Интерфейс для обработки кликов на элементах списка.
     */
    public interface OnItemClickListener {
        void onItemClick(int teacherId, ImageView infoTeacher);
    }
}
