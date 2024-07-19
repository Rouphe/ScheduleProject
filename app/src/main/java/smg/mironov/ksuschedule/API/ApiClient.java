package smg.mironov.ksuschedule.API;

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
    private static final String BASE_URL = "http://77.232.128.111:8081/";
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

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}
