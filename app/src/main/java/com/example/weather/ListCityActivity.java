package com.example.weather;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.example.weather.databinding.ActivityListCityBinding;
import com.example.weather.databinding.ActivityMainBinding;

public class ListCityActivity extends AppCompatActivity {
    private ActivityListCityBinding binding;
    private SearchView searchView;
    private ImageButton btn_active_search;
    private ActivityResultLauncher<Intent> searchLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        searchLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        });

        btn_active_search = findViewById(R.id.btn_search);
        btn_active_search.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            searchLauncher.launch(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Xử lý sự kiện khi nhấn nút quay lại
        return true;
    }
}
