package smg.mironov.ksuschedule;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Adapters.TeacherAdapter;
import smg.mironov.ksuschedule.Models.TeacherDto;
import smg.mironov.ksuschedule.Models.User;

/**
 * Класс {@link TeachersActivity} отвечает за отображение списка преподавателей и информацию о каждом преподавателе.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class TeachersActivity extends AppCompatActivity {

    /** Список преподавателей */
    private ListView listView;
    /** Адаптер для отображения преподавателей */
    private TeacherAdapter adapter;
    /** Список объектов TeacherDto */
    private List<TeacherDto> teacherList;

    /** Токен аутентификации */
    private String token;
    /** Должность преподавателя */
    private String post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_teacher_screen);

        listView = findViewById(R.id.list_teachers);
        teacherList = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);

        adapter = new TeacherAdapter(this, teacherList, new TeacherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int teacherId, ImageView profileImageView) {
                getTeacherById(teacherId, new OnTeacherPostReceived() {
                    @Override
                    public void onPostReceived(String post) {
                        getUserByTeacherId(teacherId, profileImageView, post);
                    }
                });
            }
        }, token);

        listView.setAdapter(adapter);

        ImageView navButton1 = findViewById(R.id.home_icon);
        ImageView navButton2 = findViewById(R.id.profile_icon);

        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen1();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToScreen2();
            }
        });

        fetchTeachersFromServer();
    }

    private PopupWindow popupWindow;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * Метод для получения информации о пользователе по идентификатору преподавателя.
     *
     * @param teacherId идентификатор преподавателя
     * @param profileImageView ImageView для отображения фотографии профиля
     * @param post должность преподавателя
     */
    private void getUserByTeacherId(int teacherId, final ImageView profileImageView, String post) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getTeacherUserByTeacherId(token, teacherId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    showInfo(user, profileImageView, post);
                } else {
                    String error = "Преподаватель еще не зарегистрирован";
                    Toast.makeText(TeachersActivity.this, error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TeachersActivity.this, "Ошибка при выполнении запроса: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Метод для отображения информации о преподавателе.
     *
     * @param user объект {@link User}, представляющий преподавателя
     * @param profileImageView ImageView для отображения фотографии профиля
     * @param post должность преподавателя
     */
    private void showInfo(User user, ImageView profileImageView, String post) {
        Log.d(TAG, "Showing info for user: " + user.toString());
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.teacher_info_popup, null);

        TextView popupLastName = popupView.findViewById(R.id.teacherLastname);
        TextView popupFirstName = popupView.findViewById(R.id.teacherFirstName);
        TextView popupMiddleName = popupView.findViewById(R.id.teacherMiddleName);
        TextView popupPost = popupView.findViewById(R.id.post);
        TextView popupInfo = popupView.findViewById(R.id.teacherInfo);
        ImageView popupPhoto = popupView.findViewById(R.id.teacherPhoto);

        popupLastName.setText(user.getLastName());
        popupFirstName.setText(user.getFirstName());
        popupMiddleName.setText(user.getMiddleName());
        popupPost.setText(post);
        popupInfo.setText(user.getInfo());

        fetchTeacherPhotoFromServer(user.getId(), popupPhoto, profileImageView);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);

        addDimBackground(popupWindow);

        popupWindow.showAtLocation(profileImageView, Gravity.CENTER, 0, 0);

        ImageView closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            profileImageView.setEnabled(true);
            profileImageView.setVisibility(View.VISIBLE);
            removeDimBackground();
        });
    }

    /**
     * Метод для добавления затемненного фона при открытии всплывающего окна.
     *
     * @param popupWindow объект {@link PopupWindow}
     */
    private void addDimBackground(PopupWindow popupWindow) {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content).getRootView();
        View dimView = new View(this);
        dimView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        dimView.setBackgroundColor(Color.parseColor("#80000000"));
        root.addView(dimView);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                root.removeView(dimView);
            }
        });
    }

    /**
     * Метод для удаления затемненного фона при закрытии всплывающего окна.
     */
    private void removeDimBackground() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content).getRootView();
        View dimView = root.findViewWithTag("dim_view");
        if (dimView != null) {
            root.removeView(dimView);
        }
    }

    /**
     * Метод для получения фотографии преподавателя с сервера.
     *
     * @param userId идентификатор пользователя
     * @param popupPhoto ImageView для отображения фотографии во всплывающем окне
     * @param profileImageView ImageView для отображения фотографии в списке
     */
    private void fetchTeacherPhotoFromServer(Long userId, final ImageView popupPhoto, final ImageView profileImageView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getUserTeacherPhoto(token, userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isDestroyed()) {
                    if (response.isSuccessful() && response.body() != null) {
                        refreshTokenIfNeeded(response);
                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                        popupPhoto.setImageBitmap(bitmap);
                        profileImageView.setImageBitmap(bitmap);
                        adapter.updateTeacherPhoto(userId, bitmap);
                        saveImageToInternalStorage(bitmap, userId);
                    } else {
                        Toast.makeText(TeachersActivity.this, "Ошибка загрузки фото преподавателя", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!isDestroyed()) {
                    Toast.makeText(TeachersActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Метод для получения преподавателя по его идентификатору.
     *
     * @param teacherId идентификатор преподавателя
     * @param callback объект {@link OnTeacherPostReceived} для получения должности преподавателя
     */
    private void getTeacherById(int teacherId, OnTeacherPostReceived callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<TeacherDto> teacherCall = apiService.getTeacherById(token, teacherId);
        teacherCall.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    TeacherDto teacher = response.body();
                    callback.onPostReceived(teacher.getPost());
                } else {
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных о преподавателе", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {
                Toast.makeText(TeachersActivity.this, "Ошибка при выполнении запроса: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для получения списка преподавателей с сервера.
     */
    private void fetchTeachersFromServer() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://24schedule.ru:80/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<TeacherDto>> call = apiService.getAllTeachers();
        call.enqueue(new Callback<List<TeacherDto>>() {
            @Override
            public void onResponse(Call<List<TeacherDto>> call, Response<List<TeacherDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    teacherList.clear();
                    teacherList.addAll(response.body());

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TeachersActivity.this, "Ошибка при получении данных: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TeacherDto>> call, Throwable t) {
                Toast.makeText(TeachersActivity.this, "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Метод для переключения на главный экран.
     */
    private void switchToScreen1() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для переключения на экран профиля.
     */
    private void switchToScreen2() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для обновления токена аутентификации, если это необходимо.
     *
     * @param response объект {@link Response}, содержащий ответ сервера
     */
    private void refreshTokenIfNeeded(Response<?> response) {
        String newToken = response.headers().get("Authorization");
        if (newToken != null && newToken.startsWith("Bearer ")) {
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("auth_token", newToken.substring(7));
            editor.apply();
        }
    }

    /**
     * Метод для сохранения изображения во внутреннее хранилище.
     *
     * @param bitmap объект {@link Bitmap}, представляющий изображение
     * @param userId идентификатор пользователя
     */
    private void saveImageToInternalStorage(Bitmap bitmap, Long userId) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, "profile_" + userId + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для загрузки изображения из внутреннего хранилища.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link Bitmap}, представляющий изображение
     */
    private Bitmap loadImageFromInternalStorage(int userId) {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f = new File(directory, "profile_" + userId + ".jpg");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Интерфейс для получения должности преподавателя.
     */
    public interface OnTeacherPostReceived {
        void onPostReceived(String post);
    }
}
