package com.example.weather.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.example.weather.R;
import com.example.weather.databinding.ActivityListCityBinding;

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
