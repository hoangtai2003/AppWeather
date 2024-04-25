package com.example.weather.network;

import com.example.weather.model.WeatherApp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("weather")
    Call<WeatherApp> getweatherData(
            @Query("q") String city,
            @Query("appid") String appid,
            @Query("unit") String units
    );
}