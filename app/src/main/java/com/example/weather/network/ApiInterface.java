package com.example.weather.network;

import com.example.weather.model.WeatherApp;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    ApiInterface apiInterface = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface.class);
    @GET("weather")
    Call<WeatherApp> getweatherData(
            @Query("q") String city,
            @Query("appid") String appid,
            @Query("unit") String units
    );
}
