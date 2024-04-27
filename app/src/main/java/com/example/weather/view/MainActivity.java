package com.example.weather.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.model.City;
import com.example.weather.R;
import com.example.weather.databinding.ActivityMainBinding;
import com.example.weather.model.WeatherApp;
import com.example.weather.network.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ImageButton btn_active_list;
    private ActivityResultLauncher<Intent> listLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchCity();

        listLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        btn_active_list = binding.btnActionList;
        btn_active_list.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListCityActivity.class);
            listLauncher.launch(intent);
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("weatherData")) {
            WeatherApp weatherApp = (WeatherApp) intent.getSerializableExtra("weatherData");
            if (weatherApp != null) {
                double temperature = weatherApp.getMain().getTemp();
                int humidity = weatherApp.getMain().getHumidity();
                double windSpeed = weatherApp.getWind().getSpeed();
                long sunRise = weatherApp.getSys().getSunrise();
                long sunSet = weatherApp.getSys().getSunset();
                int seaLevel = weatherApp.getMain().getPressure();
                String condition = weatherApp.getWeather().get(0).getMain();
                double maxTemp = weatherApp.getMain().getTemp_max();
                double minTemp = weatherApp.getMain().getTemp_min();

                binding.temp.setText(formatTemperature(temperature) + " ℃");
                binding.weather.setText(condition);
                binding.maxTemp.setText("Max Temp: " + formatTemperature(maxTemp) + " ℃");
                binding.minTemp.setText("Min Temp: " + formatTemperature(minTemp) + " ℃");
                binding.humidity.setText(humidity + " %");
                binding.wind.setText(windSpeed + " m/s");
                binding.sunrise.setText(time(sunRise));
                binding.sunset.setText(time(sunSet));
                binding.sea.setText(seaLevel + " hpa");
                binding.condition.setText(condition);
                binding.day.setText(dayName(System.currentTimeMillis()));
                binding.date.setText(date());
                binding.cityName.setText(weatherApp.getName());
                changeImageAccordingToWeatherCondition(condition);
            }
        }
        else if (intent != null && intent.hasExtra("city")) {
            City city = (City) intent.getSerializableExtra("city");
            if (city != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchWeatherData(city.getCityName());
                    }
                }, 300);
            }
        }
        else if (intent != null && intent.hasExtra("Fail")) {
            String fail = intent.getStringExtra("Fail");
            binding.cityName.setText(fail);
        }
    }

    private void searchCity() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    fetchWeatherData(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void fetchWeatherData(String cityName) {
        ApiInterface.apiInterface.getweatherData(cityName, "9d25492b3c5d467f46369b1d01a67d7a", "metric").enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    double temperature = responseBody.getMain().getTemp();
                    int humidity = responseBody.getMain().getHumidity();
                    double windSpeed = responseBody.getWind().getSpeed();
                    long sunRise = responseBody.getSys().getSunrise();
                    long sunSet = responseBody.getSys().getSunset();
                    int seaLevel = responseBody.getMain().getPressure();
                    String condition = responseBody.getWeather().get(0).getMain();
                    double maxTemp = responseBody.getMain().getTemp_max();
                    double minTemp = responseBody.getMain().getTemp_min();

                    binding.temp.setText(formatTemperature(temperature) + " ℃");
                    binding.weather.setText(condition);
                    binding.maxTemp.setText("Max Temp: " + formatTemperature(maxTemp) + " ℃");
                    binding.minTemp.setText("Min Temp: " + formatTemperature(minTemp) + " ℃");
                    binding.humidity.setText(humidity + " %");
                    binding.wind.setText(windSpeed + " m/s");
                    binding.sunrise.setText(time(sunRise));
                    binding.sunset.setText(time(sunSet));
                    binding.sea.setText(seaLevel + " hpa");
                    binding.condition.setText(condition);
                    binding.day.setText(dayName(System.currentTimeMillis()));
                    binding.date.setText(date());
                    binding.cityName.setText(responseBody.getName());
                    changeImageAccordingToWeatherCondition(condition);
                } else {
                    binding.cityName.setText("City not found");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApp> call, @NonNull Throwable t) {
                binding.cityName.setText("City not found");
            }
        });
    }

    private void changeImageAccordingToWeatherCondition(String condition) {
        switch (condition) {
            case "Sunny":
                binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
                break;
            case "Partly Clouds":
            case "Clouds":
            case "Overcast":
            case "Mist":
            case "Foggy":
                binding.getRoot().setBackgroundResource(R.drawable.colud_background);
                binding.lottieAnimationView.setAnimation(R.raw.cloud);
                break;
            case "Light Rain":
            case "Drizzle":
            case "Moderate Rain":
            case "Showers":
            case "Heavy Rain":
            case "Rain":
                binding.getRoot().setBackgroundResource(R.drawable.rain_background);
                binding.lottieAnimationView.setAnimation(R.raw.rain);
                break;
            case "Light Snow":
            case "Moderate Snow":
            case "Heavy Snow":
            case "Blizzard":
                binding.getRoot().setBackgroundResource(R.drawable.snow_background);
                binding.lottieAnimationView.setAnimation(R.raw.snow);
                break;
            case "Clear":
            case "Clear Sky":
                binding.getRoot().setBackgroundResource(R.drawable.clear_background);
                binding.lottieAnimationView.setAnimation(R.raw.clear);
                break;
            default:
                binding.getRoot().setBackgroundResource(R.drawable.sunny_background);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
        }
        binding.lottieAnimationView.playAnimation();
    }

    private String date() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String time(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000));
    }

    private String dayName(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String formatTemperature(double temperature) {
        return String.format(Locale.getDefault(), "%.2f", temperature / 10).replace(",", ".");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // Kết thúc tất cả các hoạt động và thoát ứng dụng
    }

}
