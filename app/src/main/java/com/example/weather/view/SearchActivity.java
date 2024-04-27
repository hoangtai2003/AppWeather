package com.example.weather.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.databinding.ActivityMainBinding;
import com.example.weather.databinding.ActivitySearchBinding;
import com.example.weather.model.City;
import com.example.weather.adapter.CityAdapter;
import com.example.weather.R;
import com.example.weather.model.WeatherApp;
import com.example.weather.network.ApiInterface;
import com.example.weather.roomdatabase.AppDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rcvCities;
    private CityAdapter cityAdapter;
    private SearchView searchView;
    private List<City> cityList = new ArrayList<>();
    private int iPosistion;
    private ActivitySearchBinding binding;
    private AppDatabase db;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Tìm kiếm thành phố");

        rcvCities = binding.rcvCities;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvCities.setLayoutManager(linearLayoutManager);

        cityAdapter = new CityAdapter(this, cityList, new CityAdapter.ClickListeners() {
            @Override
            public void onItemClick(int position, View v) {
                iPosistion = position;
                City city = cityList.get(position);
                fetchWeatherDataMain(city.getCityName());
            }

            @Override
            public void onAddCityClicked(int position) {
                Intent intent = new Intent();

                City city = cityList.get(position);
                db = AppDatabase.getDatabase(SearchActivity.this);
                handler = new Handler(Looper.getMainLooper());
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    long newCid = db.cityDao().insert(city);
                    handler.post(() -> {
                        intent.putExtra("cityName", city.getCityName());
                        intent.putExtra("cityCountry", city.getCountryName());
                        intent.putExtra("cid", newCid);
                        setResult(RESULT_OK, intent);
                        SearchActivity.this.finish();
                    });
                });

            }
        });
        rcvCities.setAdapter(cityAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvCities.addItemDecoration(itemDecoration);

        // Mở SearchView và focus vào nó
        openAndFocusSearchView();
    }

    private void addCityToList(City city) {
        cityList.add(city);
        cityAdapter.notifyDataSetChanged();
    }

    private void showCityNotFoundMessage() {
        Snackbar.make(findViewById(android.R.id.content), "City not found", Snackbar.LENGTH_SHORT).show();
    }

    private void fetchWeatherDataMain(String cityName) {
        ApiInterface.apiInterface.getweatherData(cityName, "9d25492b3c5d467f46369b1d01a67d7a", "metric").enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                    intent.putExtra("weatherData", responseBody);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getBaseContext(),"City not found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApp> call, @NonNull Throwable t) {

            }
        });
    }

    private void fetchWeatherDataAdd(String cityName) {
        ApiInterface.apiInterface.getweatherData(cityName, "9d25492b3c5d467f46369b1d01a67d7a", "metric").enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    if (responseBody.getName() != null && responseBody.getSys().getCountry() != null) {
                        // Tạo đối tượng City từ cityName
                        City city = new City(responseBody.getName(), responseBody.getSys().getCountry());

                        // Kiểm tra xem thành phố đã tồn tại trong danh sách chưa
                        boolean cityExists = false;
                        for (City existingCity : cityList) {
                            if (existingCity.getCityName().trim().equalsIgnoreCase(city.getCityName().trim())) {
                                cityExists = true;
                                break;
                            }
                        }

                        // Nếu thành phố chưa tồn tại, thêm vào danh sách
                        if (!cityExists) {
                            addCityToList(city);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherApp> call, @NonNull Throwable t) {
                showCityNotFoundMessage();
            }
        });
    }

    private void openAndFocusSearchView() {
        // Sử dụng Handler để đảm bảo rằng SearchView được mở và focus sau khi layout hoàn thành
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (searchView != null) {
                    // Mở SearchView
                    searchView.setIconified(false);
                    // Focus vào SearchView
                    searchView.requestFocus();
                }
            }
        }, 200); // Đợi 200ms trước khi mở SearchView để đảm bảo layout đã hoàn thành
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Hủy bỏ các tác vụ trước đó trên handler để trì hoãn gọi API
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                // Tạo runnable mới để gọi API sau 300ms mỗi khi người dùng nhập
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        // Kiểm tra newText có trống không
                        if (newText.isEmpty()) {
                            // Xóa toàn bộ thành phố trong cityList
                            cityList.clear();
                            cityAdapter.notifyDataSetChanged();
                            return;
                        }

                        fetchWeatherDataAdd(newText);

                        // Loại bỏ các thành phố không phù hợp
                        List<City> citiesToRemove = new ArrayList<>();
                        for (City city : cityList) {
                            if (!city.getCityName().toLowerCase().trim().contains(newText.toLowerCase().trim())) {
                                citiesToRemove.add(city);
                            }
                        }
                        cityList.removeAll(citiesToRemove);
                        cityAdapter.notifyDataSetChanged();
                    }
                };

                // Trì hoãn gọi API sau 300ms
                handler.postDelayed(runnable, 300);

                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Xử lý sự kiện khi nhấn nút quay lại
        return true;
    }
}
