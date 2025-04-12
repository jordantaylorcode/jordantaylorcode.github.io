package com.zybooks.jordaninventoryapp.repo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InventoryRepository {
    private static HTTPService service = null;
    private static InventoryRepository instance;
    private final static String TAG = "Database";


    private InventoryRepository() {

        // this is a singleton class that provides an instance of the HTTP methods
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://4a8kld1cr7.execute-api.us-east-2.amazonaws.com/dev/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(HTTPService.class);
    }

    public static synchronized InventoryRepository getInstance() {
        if (instance == null) {
            instance = new InventoryRepository();
        }
        return instance;
    }

    public HTTPService getService() {
        return service;
    }
}
