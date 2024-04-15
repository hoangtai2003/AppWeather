package com.example.weather;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.weather.databinding.ActivityMainBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetWeatherDate("Vietnam");
        searchCity();
    }

    private void searchCity() {
        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null) {
                    fetWeatherDate(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    private void fetWeatherDate(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<WeatherApp> response = apiInterface.getweatherData(cityName, "9d25492b3c5d467f46369b1d01a67d7a", "metric");
        response.enqueue(new Callback<WeatherApp>() {
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
                    binding.cityName.setText(cityName);
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
}
