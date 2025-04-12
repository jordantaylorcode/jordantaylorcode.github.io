package com.zybooks.jordaninventoryapp.repo;

import com.zybooks.jordaninventoryapp.BuildConfig;
import com.zybooks.jordaninventoryapp.model.Item;
import com.zybooks.jordaninventoryapp.model.ApiResponse;
import com.zybooks.jordaninventoryapp.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface HTTPService {

    // api key retrieved from the build configuration and properties which are
    // out of the scope of the git repository
    String apiKey = BuildConfig.API_KEY;

    //retrofit methods for HTTP calls
    @Headers("x-api-key: " + apiKey)
    @POST("user")
    Call<ApiResponse> createUser(@Body User user);

    @Headers("x-api-key: " + apiKey)
    @POST("login")
    Call<ApiResponse> checkUserExists(@Body User user);

    @Headers("x-api-key: " + apiKey)
    @POST("item")
    Call<ApiResponse> createItem(@Body Item item);

    @Headers("x-api-key: " + apiKey)
    @GET("items")
    Call<ApiResponse> getItems();

    @Headers("x-api-key: " + apiKey)
    @PATCH("item")
    Call<ApiResponse> updateItem(@Body Item item);

    @Headers("x-api-key: " + apiKey)
    @HTTP(method = "DELETE", path = "item", hasBody = true)
    Call<ApiResponse> deleteItem(@Body Item item);
}
