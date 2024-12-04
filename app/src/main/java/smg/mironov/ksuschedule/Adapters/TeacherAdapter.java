package smg.mironov.ksuschedule.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.Models.User;
import smg.mironov.ksuschedule.R;

public class TeacherAdapter extends ArrayAdapter<TeacherDto> implements Filterable {

    private OnItemClickListener clickListener;
    private String token;
    private List<TeacherDto> originalList;
    private List<TeacherDto> filteredList;
    private TeacherFilter filter;
    private Map<Long, Bitmap> photoCache = new HashMap<>(); // Кеш для фотографий преподавателей

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
        if (filter == null) {
            filter = new TeacherFilter();
        }
        return filter;
    }

    private class TeacherFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<TeacherDto> filtered = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(originalList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (TeacherDto teacher : originalList) {
                    if (teacher.getName().toLowerCase().contains(filterPattern) ||
                            teacher.getPost().toLowerCase().contains(filterPattern)) {
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

    public void updateData(List<TeacherDto> teachers) {
        originalList.clear();
        originalList.addAll(teachers);
        filteredList.clear();
        filteredList.addAll(teachers);
        notifyDataSetChanged();
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

        final TeacherDto teacher = getItem(position);

        if (teacher != null) {
            viewHolder.nameTextView.setText(teacher.getName());
            viewHolder.positionTextView.setText(teacher.getPost());

            // Загружаем фото преподавателя
            loadTeacherPhoto(teacher, viewHolder.profileImageView);

            // Обрабатываем клики
            viewHolder.getInfo.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(teacher.getName(), viewHolder.profileImageView);
                }
            });
        }

        return convertView;
    }

    private void loadTeacherPhoto(TeacherDto teacher, ImageView imageView) {
        // Проверка кеша
        if (photoCache.containsKey(teacher.getId())) {
            imageView.setImageBitmap(photoCache.get(teacher.getId()));
        } else {
            // Загружаем фотографию через API
            fetchTeachersProfileId(teacher.getName(), teacher.getId(), imageView);
        }
    }

    private void fetchTeachersProfileId(String name, long userId, ImageView imageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserByFullName(token, name);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if ("TEACHER".equals(user.getRole())) {
                        loadTeacherPhotoFromServer(user.getId(), imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TeacherAdapter", "Ошибка сети: " + t.getMessage());
                imageView.setImageResource(R.drawable.placeholder_image);
            }
        });
    }

    private void loadTeacherPhotoFromServer(Long teacherId, ImageView imageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getProfileImage(token, teacherId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        if (bitmap != null) {
                            // Сохраняем в кеш
                            photoCache.put(teacherId, bitmap);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(R.drawable.placeholder_image);
                        }
                    } catch (Exception e) {
                        Log.e("TeacherAdapter", "Ошибка загрузки фото: " + e.getMessage());
                        imageView.setImageResource(R.drawable.placeholder_image);
                    }
                } else {
                    Log.e("TeacherAdapter", "Не удалось загрузить фото: " + response.code());
                    imageView.setImageResource(R.drawable.placeholder_image);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("TeacherAdapter", "Ошибка сети: " + t.getMessage());
                imageView.setImageResource(R.drawable.placeholder_image);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(String fullName, ImageView infoTeacher);
    }
}
