package smg.mironov.ksuschedule.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс для создания клиента API с использованием Retrofit.
 *
 * @version 1.0
 * @author
 * Егор Гришанов
 * Александр Миронов
 */
public class ApiClient {
    private static final String BASE_URL = "http://24schedule.ru:80";
    private static Retrofit retrofit;



    /**
     * Возвращает экземпляр Retrofit для выполнения сетевых запросов.
     *
     * @return экземпляр Retrofit
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    /**
     * Возвращает экземпляр Retrofit с пользовательским временем ожидания.
     *
     * @param connectTimeout Таймаут подключения
     * @param writeTimeout Таймаут записи
     * @param readTimeout Таймаут чтения
     * @return экземпляр Retrofit с пользовательскими таймаутами
     */
    public static Retrofit getClientWithTimeouts(long connectTimeout, long writeTimeout, long readTimeout) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(logging);

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }


}
