package com.example.weather.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.adapter.CityAdapter;
import com.example.weather.adapter.CityAdapterList;
import com.example.weather.databinding.ActivityListCityBinding;
import com.example.weather.model.City;
import com.example.weather.model.WeatherApp;
import com.example.weather.network.ApiInterface;
import com.example.weather.roomdatabase.AppDatabase;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCityActivity extends AppCompatActivity {
    private ActivityListCityBinding binding;
    private CityAdapterList cityAdapterList;
    private RecyclerView rcv_cities;
    private SearchView searchView;
    private int iPosistion;
    private ImageButton btn_active_search;
    private AppDatabase db;
    private ArrayList<City> cityList;
    private Handler hnHandler;
    private ActivityResultLauncher<Intent> searchLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prepareCityData();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        searchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result != null && result.getResultCode() == RESULT_OK) {
                //lay ve gia tri tai result
                String cityName = result.getData().getStringExtra("cityName");
                String cityCountry = result.getData().getStringExtra("cityCountry");
                int cid = result.getData().getIntExtra("cid", 0);
                City city = new City(cityName, cityCountry);
                city.setCid(cid);
                cityList.add(0, city);
                cityAdapterList.notifyItemInserted(0);
            }
        });

        btn_active_search = binding.btnSearch;
        btn_active_search.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            searchLauncher.launch(intent);
        });

    }

    private void prepareCityData(){
        try {
            db = AppDatabase.getDatabase(this);
            cityList = new ArrayList<>();
            hnHandler = new Handler(Looper.getMainLooper());

            AppDatabase.databaseWriteExecutor.execute(() -> {
                cityList = (ArrayList<City>) db.cityDao().getAllCity();
                hnHandler.post(() -> {
                   cityAdapterList = new CityAdapterList(this, cityList, new CityAdapterList.ClickListeners() {
                       @Override
                       public void onItemClick(int position, View v) {
//                           Toast.makeText(getApplicationContext(), arrayList.get(position).getCityName(), Toast.LENGTH_SHORT).show();
                           iPosistion = position;
                           City city = cityList.get(position);
                           fetchWeatherDataMain(city.getCityName());
                       }

                       @Override
                       public void onItemLongClick(int position, View v) {
                           Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Bạn có chắc muốn xóa thành phố này?", Snackbar.LENGTH_SHORT);
                           mySnackbar.setAction("Xác nhận", view -> {
                               AppDatabase.databaseWriteExecutor.execute(() -> {
                                   City deletedCity = cityList.get(position);
                                   db.cityDao().delete(deletedCity);
                                   hnHandler.post(() -> {
                                       cityList.remove(position);
                                       cityAdapterList.notifyItemRemoved(position);
                                       showUndoCity(position, deletedCity);
                                   });
                               });
                           });
                           mySnackbar.show();
                       }
                   });
                   // set dữ liệu cho rcv

                    rcv_cities = binding.rcvCities;
                    rcv_cities.setAdapter(cityAdapterList);

                    rcv_cities.addItemDecoration(new DividerItemDecoration(rcv_cities.getContext(), DividerItemDecoration.VERTICAL));
                    rcv_cities.setLayoutManager(new LinearLayoutManager(this));
                });
            });
        } catch (Exception ex) {
            Log.e("Get all city: ", ex.getMessage());
        }
    }

    public void showUndoCity(int position, City city) {
        try {
            Snackbar mySnackbar = Snackbar.make(binding.parentLayout, "Bấm để hoàn tác.", Snackbar.LENGTH_SHORT);
            mySnackbar.setAction("Hoàn tác", v -> {
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    int newCid = (int) db.cityDao().insert(city);
                    city.setCid(newCid);
                    hnHandler.post(() -> {
                        cityList.add(position, city);
                        cityAdapterList.notifyItemInserted(position);
                    });
                });
            });
            mySnackbar.show();
        } catch (Exception ex) {
            Log.e("ShowUndoCity", ex.getMessage());
        }
    }

    private void fetchWeatherDataMain(String cityName) {
        ApiInterface.apiInterface.getweatherData(cityName, "9d25492b3c5d467f46369b1d01a67d7a", "metric").enqueue(new Callback<WeatherApp>() {
            @Override
            public void onResponse(@NonNull Call<WeatherApp> call, @NonNull Response<WeatherApp> response) {
                WeatherApp responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    Intent intent = new Intent(ListCityActivity.this, MainActivity.class);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Xử lý sự kiện khi nhấn nút quay lại
        return true;
    }
}
