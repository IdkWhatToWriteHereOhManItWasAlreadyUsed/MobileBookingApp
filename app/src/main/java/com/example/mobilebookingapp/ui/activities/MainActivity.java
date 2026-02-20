package com.example.mobilebookingapp.ui.activities;

import android.app.AlertDialog;
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
import com.example.mobilebookingapp.caching.CacheManager;
import com.example.mobilebookingapp.network.NetworkUtils;
import com.example.mobilebookingapp.utils.SortUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private List<HotelData> hotelList;
    private MaterialButton btnSearch;
    private MaterialButton btnSort;
    private CacheManager cacheManager;
    private SortUtils.SortBy currentSort = SortUtils.SortBy.RATING_DESC; // По умолчанию

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
        btnSort = findViewById(R.id.btnSort);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация кэша
        cacheManager = new CacheManager(this);
        hotelList = new ArrayList<>();

        adapter = new HotelAdapter(hotelList, hotel -> {
            Intent intent = new Intent(MainActivity.this, HotelDetailActivity.class);
            intent.putExtra("hotel_data", hotel);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                showNoInternetDialog();
                return;
            }
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            searchLauncher.launch(intent);
        });

        btnSort.setOnClickListener(v -> showSortDialog());

        // Загружаем данные при старте
        loadInitialData();
    }

    private void showSortDialog() {
        String[] sortOptions = {
                SortUtils.getSortDisplayName(SortUtils.SortBy.NAME_ASC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.NAME_DESC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.RATING_ASC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.RATING_DESC)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Сортировать отели")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentSort = SortUtils.SortBy.NAME_ASC;
                            break;
                        case 1:
                            currentSort = SortUtils.SortBy.NAME_DESC;
                            break;
                        case 2:
                            currentSort = SortUtils.SortBy.RATING_ASC;
                            break;
                        case 3:
                            currentSort = SortUtils.SortBy.RATING_DESC;
                            break;
                    }
                    applySort();
                })
                .show();
    }

    private void applySort() {
        if (hotelList.isEmpty()) {
            Toast.makeText(this, "Нет отелей для сортировки", Toast.LENGTH_SHORT).show();
            return;
        }

        // Сортируем список
        SortUtils.sortHotels(hotelList, currentSort);
        adapter.notifyDataSetChanged();

        // Показываем уведомление
        Snackbar.make(findViewById(android.R.id.content),
                "Отсортировано: " + SortUtils.getSortDisplayName(currentSort),
                Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Загрузка начальных данных: сначала кэш, потом попытка обновить из сети
     */
    private void loadInitialData() {
        // 1. Сначала показываем кэшированные данные (если есть)
        List<HotelData> cachedHotels = cacheManager.getHotels();
        if (!cachedHotels.isEmpty()) {
            hotelList.clear();
            hotelList.addAll(cachedHotels);
            applySort(); // Сортируем при загрузке

            String cacheInfo = "Загружено из кэша";
            if (cacheManager.isCacheExpired()) {
                cacheInfo += " (кэш устарел)";
            }
            Toast.makeText(this, cacheInfo, Toast.LENGTH_SHORT).show();
        }

        // 2. Если есть интернет - пробуем обновить данные
        if (NetworkUtils.isNetworkAvailable(this)) {
            refreshDataFromNetwork();
        } else {
            if (cachedHotels.isEmpty()) {
                // Нет ни интернета, ни кэша
                Toast.makeText(this,
                        "Нет подключения к интернету и нет сохраненных данных",
                        Toast.LENGTH_LONG).show();
            } else {
                // Показываем уведомление что данные из кэша
                Snackbar.make(findViewById(android.R.id.content),
                        "Работаем в оффлайн-режиме. Данные могут быть устаревшими.",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Обновление данных из сети
     */
    private void refreshDataFromNetwork() {
        HotelsLoader.searchHotelsAsync(null, null, null, null, null, null, 20, null,
                new HotelsLoader.HotelsCallback() {
                    @Override
                    public void onSuccess(List<HotelData> hotels) {
                        runOnUiThread(() -> {
                            if (!hotels.isEmpty()) {
                                // Обновляем список
                                hotelList.clear();
                                hotelList.addAll(hotels);
                                applySort(); // Сортируем новые данные

                                // Сохраняем в кэш
                                cacheManager.saveHotels(hotels);

                                Toast.makeText(MainActivity.this,
                                        "Данные обновлены из сети", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            // Если ошибка, но у нас уже есть кэш - ничего не делаем
                            if (!cacheManager.hasCache()) {
                                Toast.makeText(MainActivity.this,
                                        "Ошибка загрузки: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void performSearchWithParams(Map<String, String> params) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            // Если нет интернета - показываем кэш (если есть)
            List<HotelData> cachedHotels = cacheManager.getHotels();
            if (!cachedHotels.isEmpty()) {
                hotelList.clear();
                hotelList.addAll(cachedHotels);
                applySort();
                Toast.makeText(this, "Оффлайн-режим: показаны кэшированные данные",
                        Toast.LENGTH_SHORT).show();
            } else {
                showNoInternetDialog();
            }
            return;
        }

        String city = params.get("city");
        String country = params.get("country");
        String countryCode = params.get("country_code");
        String name = params.get("name");
        Integer minRating = params.containsKey("min_rating") ?
                Integer.parseInt(params.get("min_rating")) : null;
        Integer limit = params.containsKey("limit") ?
                Integer.parseInt(params.get("limit")) : null;

        Toast.makeText(this, "Поиск...", Toast.LENGTH_SHORT).show();

        HotelsLoader.searchHotelsAsync(city, country, countryCode, name,
                null, minRating, limit, null, new HotelsLoader.HotelsCallback() {
                    @Override
                    public void onSuccess(List<HotelData> hotels) {
                        runOnUiThread(() -> {
                            hotelList.clear();
                            hotelList.addAll(hotels);
                            applySort(); // Сортируем результаты поиска

                            // Сохраняем результат поиска в кэш
                            if (!hotels.isEmpty()) {
                                cacheManager.saveHotels(hotels);
                            }

                            String message = hotels.isEmpty()
                                    ? "Ничего не найдено"
                                    : "Найдено отелей: " + hotels.size();
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            // При ошибке показываем кэш
                            List<HotelData> cachedHotels = cacheManager.getHotels();
                            if (!cachedHotels.isEmpty()) {
                                hotelList.clear();
                                hotelList.addAll(cachedHotels);
                                applySort();
                                Toast.makeText(MainActivity.this,
                                        "Ошибка сети. Показаны кэшированные данные",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Ошибка поиска: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Нет интернета")
                .setMessage("Для поиска отелей необходимо подключение к интернету. Хотите посмотреть сохраненные данные?")
                .setPositiveButton("Показать кэш", (dialog, which) -> {
                    List<HotelData> cachedHotels = cacheManager.getHotels();
                    if (!cachedHotels.isEmpty()) {
                        hotelList.clear();
                        hotelList.addAll(cachedHotels);
                        applySort();
                        Toast.makeText(this, "Показаны кэшированные данные",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Нет сохраненных данных",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Настройки", (dialog, which) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                })
                .setNeutralButton("Отмена", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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