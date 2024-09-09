package smg.mironov.ksuschedule;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smg.mironov.ksuschedule.API.ApiClient;
import smg.mironov.ksuschedule.API.ApiService;
import smg.mironov.ksuschedule.Adapters.DayWeekAdapter;
import smg.mironov.ksuschedule.Models.GroupDto;
import smg.mironov.ksuschedule.Models.TeacherDto;

/**
 * Класс {@link ProfileActivity} отвечает за отображение и редактирование профиля пользователя.
 *
 * @author Егор Гришанов, Алекснадр Миронов
 *
 * @version 1.0
 */
public class ProfileActivity extends AppCompatActivity {

    /** Текстовое поле для фамилии пользователя */
    private TextView userLastname;
    /** Текстовое поле для имени пользователя */
    private TextView userFirstName;
    /** Текстовое поле для отчества пользователя */
    private TextView userMiddleName;
    /** Текстовое поле для номера группы пользователя */
    private TextView userGroupNumber;
    /** Текстовое поле для направления группы */
    private TextView userGroupDirection;
    /** Текстовое поле для профиля группы */
    private TextView userGroupProfile;
    /** Заголовок для профиля группы */
    private TextView userGroupProfileTitle;
    /** Заголовок для направления группы */
    private TextView userGroupDirectionTitle;
    /** Контейнер для дополнительной информации */
    private View additionalInfoContainer;
    /** Иконка для сворачивания и разворачивания контейнера */
    private ImageView chevronDown;
    /** Кнопка для выхода из профиля */
    private ImageView logoutButton;
    /** Кнопка для перехода к настройкам */
    private ImageView settingsButton;
    /** Фото профиля пользователя */
    private ImageView profilePhoto;
    /** Кнопка для добавления фото */
    private ImageView addPhotoButton;
    /** Верхняя часть профиля */
    private ConstraintLayout profileCap;
    /** Оригинальная высота верхней части профиля */
    private int originalHeight;
    /** Кнопка для редактирования профиля */
    private LinearLayout editButton;

    /** Токен аутентификации */
    private String token;
    /** Email пользователя */
    private String userEmail;

    /** Флаг для проверки, развернут ли контейнер */
    private boolean isExpanded = false;

    /** Константа для запроса выбора изображения */
    private static final int PICK_IMAGE_REQUEST = 1;
    /** Обработчик жестов */
    private GestureDetector gestureDetector;

    /** Адаптер для расписания */
    private DayWeekAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        token = "Bearer " + preferences.getString("auth_token", null);
        userEmail = preferences.getString("user_email", null);

        // Настройка кнопок навигационной панели
        ImageView navButton1 = findViewById(R.id.teachers_icon);
        ImageView navButton2 = findViewById(R.id.home_icon);

        userLastname = findViewById(R.id.userLastname);
        userFirstName = findViewById(R.id.userFirstName);
        userMiddleName = findViewById(R.id.userMiddleName);
        userGroupNumber = findViewById(R.id.userGroupNumber);
        additionalInfoContainer = findViewById(R.id.additional_info_container);
        chevronDown = findViewById(R.id.chevron_down);
        userGroupDirection = findViewById(R.id.userGroupDirection);
        userGroupProfile = findViewById(R.id.userGroupProfile);
        logoutButton = findViewById(R.id.logout_icon);
        settingsButton = findViewById(R.id.settings_icon);
        profilePhoto = findViewById(R.id.profilePhoto);
        addPhotoButton = findViewById(R.id.addPhoto);
        userGroupProfileTitle = findViewById(R.id.userGroupProfileTitle);
        profileCap = findViewById(R.id.profile_cap);
        chevronDown = findViewById(R.id.chevron_down);
        editButton = findViewById(R.id.editButton);
        userGroupDirectionTitle = findViewById(R.id.userGroupDirectionTitle);

        loadUserData();

        // Настройка жестов для profileCap
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        profileCap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });



        // Назначаем обработчик на кнопку изменения фото
        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        chevronDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAdditionalInfo();
                toggleAnimation();
            }
        });


        navButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на первый экран
                switchToTeachersScreen();
            }
        });

        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Логика переключения на второй экран
                switchToMain();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSettings();
            }
        });

        profileCap.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = profileCap.getHeight();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEdit();
            }
        });
    }

    /**
     * Метод для переключения на экран редактирования профиля.
     */
    private void switchToEdit() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для анимации разворачивания и сворачивания контейнера дополнительной информации.
     */
    private void toggleAnimation() {
        Log.d("ProfileActivity", "Animating profileCap, isExpanded: " + isExpanded);
        ValueAnimator valueAnimator;
        if (isExpanded) {
            valueAnimator = ValueAnimator.ofInt(profileCap.getHeight(), originalHeight);
        } else {
            valueAnimator = ValueAnimator.ofInt(profileCap.getHeight(), (int) (originalHeight * 1.50));
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = profileCap.getLayoutParams();
                layoutParams.height = (int) animation.getAnimatedValue();
                profileCap.setLayoutParams(layoutParams);
            }
        });

        valueAnimator.setDuration(300);
        valueAnimator.start();
        isExpanded = !isExpanded;
    }



    /**
     * Метод для открытия галереи для выбора изображения.
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите фото профиля"), PICK_IMAGE_REQUEST);
    }

    /**
     * Метод для переключения на экран настроек.
     */
    private void switchToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для выхода из профиля и удаления данных пользователя.
     */
    /**
     * Метод для выхода из профиля и удаления данных пользователя,
     * оставляя логин и пароль для автозаполнения при следующем входе.
     */
    private void logout() {
        // Очистить фото профиля из внутренней памяти
        deleteProfileImage();

        // Получить доступ к SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Сохраняем логин и пароль
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false);
        String savedEmail = sharedPreferences.getString("saved_email", null);
        String savedPassword = sharedPreferences.getString("saved_password", null);

        // Очищаем все данные, кроме логина и пароля
        editor.clear();

        // Восстанавливаем логин и пароль
        if (savedEmail != null && savedPassword != null) {
            editor.putString("saved_email", savedEmail);
            editor.putString("saved_password", savedPassword);
            editor.putBoolean("remember_me", true);
            editor.putBoolean("dark_mode", false);
        }

        editor.apply();

        // Очистить расписание в MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra("clear_schedule", true);
        startActivity(intent);

        // Перейти к экрану входа
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    /**
     * Метод для удаления фото профиля из внутренней памяти.
     */
    private void deleteProfileImage() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
        File path = new File(directory, "profile_photo.jpg");
    }

    /**
     * Метод для переключения на экран преподавателей.
     */
    private void switchToTeachersScreen() {
        // Логика переключения на экран преподавателей
        Intent intent = new Intent(this, TeachersActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для переключения на главный экран.
     */
    private void switchToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Метод для получения реального пути к изображению из URI.
     * @param contentUri URI изображения.
     * @return строка с реальным путем к изображению.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    /**
     * Метод для обработки выбора изображения из галереи.
     * @param requestCode код запроса.
     * @param resultCode код результата.
     * @param data данные с результатом.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profilePhoto.setImageBitmap(bitmap);

                // Отправляем изображение на сервер
                uploadImageToServer(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для загрузки данных пользователя из {@link SharedPreferences}.
     */
    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        String lastName = sharedPreferences.getString("user_lastName", "Фамилия");
        String firstName = sharedPreferences.getString("user_firstName", "Имя");
        String middleName = sharedPreferences.getString("user_middleName", "Отчество");
        String groupNumber = sharedPreferences.getString("user_groupNumber", "100");
        String role = sharedPreferences.getString("user_role", "STUDENT");

        userLastname.setText(lastName);
        userFirstName.setText(firstName);
        userMiddleName.setText(middleName);
        userGroupNumber.setText(groupNumber);

        Bitmap profileBitmap = loadImageFromInternalStorage();
        if (profileBitmap == null) {
            fetchProfileImageFromServer();
        } else {
            profilePhoto.setImageBitmap(profileBitmap);
        }

        if (role.equals("STUDENT")) {
            userGroupNumber.setText(groupNumber);
            fetchGroupInfo(groupNumber);
        } else {
            userGroupProfile.setVisibility(View.GONE);
            userGroupProfileTitle.setVisibility(View.GONE);
            userGroupDirection.setVisibility(View.GONE);
            userGroupNumber.setVisibility(View.INVISIBLE);
            userGroupDirectionTitle.setText(sharedPreferences.getString("user_info", "Информация не найдена"));
            fetchTeacherInfo(lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0) + ".");
        }
    }

    /**
     * Метод для переключения видимости контейнера дополнительной информации.
     */
    private void toggleAdditionalInfo() {
        if (additionalInfoContainer.getVisibility() == View.GONE) {
            additionalInfoContainer.setVisibility(View.VISIBLE);
            chevronDown.setImageResource(R.drawable.chevron_up);
        } else {
            additionalInfoContainer.setVisibility(View.GONE);
            chevronDown.setImageResource(R.drawable.chevron_down_white);
        }
    }

    /**
     * Метод для получения информации о группе с сервера.
     * @param groupNumber номер группы.
     */
    private void fetchGroupInfo(String groupNumber) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GroupDto> call = apiService.getGroupByNumber(token, groupNumber);

        call.enqueue(new Callback<GroupDto>() {
            @Override
            public void onResponse(Call<GroupDto> call, Response<GroupDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    GroupDto groupDto = response.body();

                    if (groupDto.getProfile() == null) {
                        userGroupProfile.setVisibility(View.GONE);
                        userGroupProfileTitle.setVisibility(View.GONE);
                    } else {
                        userGroupProfile.setText(groupDto.getProfile());
                    }

                    if (groupDto.getProfile() == null && groupDto.getDirection() == null) {
                        userGroupDirection.setText("Информация не найдена.");
                    }

                    userGroupDirection.setText(groupDto.getDirection());
                }
            }

            @Override
            public void onFailure(Call<GroupDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки информации", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для получения информации о преподавателе с сервера.
     * @param name имя преподавателя.
     */
    private void fetchTeacherInfo(String name) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TeacherDto> call = apiService.getTeacherByName(name);

        call.enqueue(new Callback<TeacherDto>() {
            @Override
            public void onResponse(Call<TeacherDto> call, Response<TeacherDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    TeacherDto teacherDto = response.body();

                    TextView groupLabel = findViewById(R.id.group_label);
                    groupLabel.setText("Должность:");
                    userGroupNumber.setText(teacherDto.getPost());
                    userGroupNumber.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<TeacherDto> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки информации о преподавателе", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для отправки изображения профиля на сервер.
     * @param imageUri URI изображения.
     */
    private void uploadImageToServer(Uri imageUri) {
        String filePath = getRealPathFromURI(imageUri);
        if (filePath == null) {
            Toast.makeText(this, "Невозможно получить путь к файлу", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.uploadProfileImage(token, userEmail, body); // Передаем email пользователя

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    refreshTokenIfNeeded(response);
                    Toast.makeText(ProfileActivity.this, "Фото профиля успешно обновлено", Toast.LENGTH_SHORT).show();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        saveImageToInternalStorage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Ошибка загрузки фото профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для получения изображения профиля с сервера.
     */
    private void fetchProfileImageFromServer() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.getProfileImage(token, userEmail); // предполагается, что API предоставляет такой метод

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    refreshTokenIfNeeded(response);
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    profilePhoto.setImageBitmap(bitmap);
                    saveImageToInternalStorage(bitmap);
                } else {
                    //Toast.makeText(ProfileActivity.this, "Ошибка загрузки фото профиля", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Метод для сохранения изображения профиля во внутреннее хранилище.
     * @param bitmap изображение профиля.
     */
    private void saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
        File path = new File(directory, "profile_photo.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для загрузки изображения профиля из внутреннего хранилища.
     * @return объект {@link Bitmap} с изображением профиля.
     */
    private Bitmap loadImageFromInternalStorage() {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("profile_images", Context.MODE_PRIVATE);
            File path = new File(directory, "profile_photo.jpg");
            return BitmapFactory.decodeStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод для обновления токена аутентификации, если это необходимо.
     * @param response объект {@link Response}, содержащий ответ сервера.
     */
    private void refreshTokenIfNeeded(Response<?> response) {
        // Проверка, есть ли новый токен в заголовке ответа
        String newToken = response.headers().get("Authorization");
        if (newToken != null && newToken.startsWith("Bearer ")) {
            SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("auth_token", newToken.substring(7)); // Сохраняем новый токен без префикса "Bearer "
            editor.apply();
        }
    }

    /**
     * Класс {@link SwipeGestureListener} для обработки жестов свайпа.
     */
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 300;
        private static final int SWIPE_VELOCITY_THRESHOLD = 300;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffY) > Math.abs(diffX)) {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        // Swipe down
                        Log.d("SwipeGestureListener", "Swipe down detected");
                        if (!isExpanded) {
                            toggleAdditionalInfo();
                            toggleAnimation();
                        }
                    } else {
                        // Swipe up
                        Log.d("SwipeGestureListener", "Swipe up detected");
                        if (isExpanded) {
                            toggleAdditionalInfo();
                            toggleAnimation();
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("SwipeGestureListener", "onScroll detected: distanceY = " + distanceY);
            return true;
        }
    }

}
