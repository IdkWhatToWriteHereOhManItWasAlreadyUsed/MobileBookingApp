package com.example.mobilebookingapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.HotelData;
import com.example.mobilebookingapp.network.HotelsLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private List<HotelData> hotelList;
    private MaterialButton btnSearch;

    private final ActivityResultLauncher<Intent> searchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Map<String, String> searchParams =
                            (Map<String, String>)
                                    result.getData().getSerializableExtra(SearchActivity.EXTRA_SEARCH_PARAMS);

                    if (searchParams != null) {
                        performSearchWithParams(searchParams);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnSearch = findViewById(R.id.btnSearch);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hotelList = new ArrayList<>();
        adapter = new HotelAdapter(hotelList, hotel -> {
            Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
            intent.putExtra("hotel_data", hotel);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            searchLauncher.launch(intent);
        });

        //loadTestHotelsAsync();
    }

    private void performSearchWithParams(Map<String, String> params) {
        String city = params.get("city");
        String country = params.get("country");
        String countryCode = params.get("country_code");
        String name = params.get("name");
        Integer minRating = params.containsKey("min_rating") ?
                Integer.parseInt(params.get("min_rating")) : null;
        Integer limit = params.containsKey("limit") ?
                Integer.parseInt(params.get("limit")) : null;

        // Показываем что ищем
        Toast.makeText(this, "Поиск...", Toast.LENGTH_SHORT).show();

        HotelsLoader.searchHotelsAsync(city, country, countryCode, name,
                null, minRating, limit, null, new HotelsLoader.HotelsCallback() {
                    @Override
                    public void onSuccess(List<HotelData> hotels) {
                        runOnUiThread(() -> {
                            hotelList.clear();
                            hotelList.addAll(hotels);
                            adapter.notifyDataSetChanged();

                            String message = hotels.isEmpty()
                                    ? "Ничего не найдено"
                                    : "Найдено отелей: " + hotels.size();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Ошибка поиска: " + error, Toast.LENGTH_SHORT).show();
                            loadTestHotelsAsync(); // ИСПРАВЛЕНО
                        });
                    }
                });
    }

    // ИСПРАВЛЕНО: теперь асинхронный метод
    private void loadTestHotelsAsync() {
        // Показываем загрузку
        Toast.makeText(this, "Загрузка тестовых данных...", Toast.LENGTH_SHORT).show();

        // Используем асинхронный метод
        HotelsLoader.searchHotelsAsync(null, null, null, null,
                null, null, null, null, new HotelsLoader.HotelsCallback() {
                    @Override
                    public void onSuccess(List<HotelData> hotels) {
                        runOnUiThread(() -> {
                            hotelList.clear();
                            hotelList.addAll(hotels);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,
                                    "Загружены тестовые данные: " + hotels.size() + " отелей",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "Ошибка загрузки тестовых данных: " + error,
                                    Toast.LENGTH_SHORT).show();
                            // Если совсем все плохо - показываем пустой список
                            hotelList.clear();
                            adapter.notifyDataSetChanged();
                        });
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}