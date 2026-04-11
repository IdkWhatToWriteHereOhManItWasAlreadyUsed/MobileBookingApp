package com.example.mobilebookingapp.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.caching.CacheManager;
import com.example.mobilebookingapp.model.HotelData;
import com.example.mobilebookingapp.network.HotelsLoader;
import com.example.mobilebookingapp.network.NetworkUtils;
import com.example.mobilebookingapp.ui.activities.HotelDetailActivity;
import com.example.mobilebookingapp.ui.activities.SearchActivity;
import com.example.mobilebookingapp.ui.adapters.HotelAdapter;
import com.example.mobilebookingapp.utils.SortUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment implements HotelAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private HotelAdapter adapter;
    private List<HotelData> hotelList;
    private MaterialButton btnSearch, btnSort;
    private CacheManager cacheManager;
    private SortUtils.SortBy currentSort = SortUtils.SortBy.RATING_DESC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotels, container, false);

        // Инициализируем ВСЕ View ЗДЕСЬ
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSort = view.findViewById(R.id.btnSort);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cacheManager = new CacheManager(getContext());
        hotelList = new ArrayList<>();
        adapter = new HotelAdapter(hotelList, this);
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(getContext())) {
                showNoInternetDialog();
                return;
            }
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        btnSort.setOnClickListener(v -> showSortDialog());

        loadInitialData();

        return view;
    }

    private void showSortDialog() {
        String[] sortOptions = {
                SortUtils.getSortDisplayName(SortUtils.SortBy.NAME_ASC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.NAME_DESC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.RATING_ASC),
                SortUtils.getSortDisplayName(SortUtils.SortBy.RATING_DESC)
        };

        new AlertDialog.Builder(getContext())
                .setTitle("Сортировать отели")
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: currentSort = SortUtils.SortBy.NAME_ASC; break;
                        case 1: currentSort = SortUtils.SortBy.NAME_DESC; break;
                        case 2: currentSort = SortUtils.SortBy.RATING_ASC; break;
                        case 3: currentSort = SortUtils.SortBy.RATING_DESC; break;
                    }
                    applySort();
                })
                .show();
    }

    private void applySort() {
        // ПРОВЕРКА: убеждаемся, что View создан
        if (getView() == null || hotelList.isEmpty()) return;

        SortUtils.sortHotels(hotelList, currentSort);
        adapter.notifyDataSetChanged();

        Snackbar.make(getView(), "Отсортировано: " + SortUtils.getSortDisplayName(currentSort),
                Snackbar.LENGTH_SHORT).show();
    }

    private void loadInitialData() {
        List<HotelData> cachedHotels = cacheManager.getHotels();
        if (!cachedHotels.isEmpty()) {
            hotelList.clear();
            hotelList.addAll(cachedHotels);
            applySort();
        }

        if (NetworkUtils.isNetworkAvailable(getContext())) {
            refreshDataFromNetwork();
        } else if (cachedHotels.isEmpty()) {
            Toast.makeText(getContext(), "Нет интернета и сохраненных данных", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshDataFromNetwork() {
        HotelsLoader.searchHotelsAsync(null, null, null, null, null, null, 20, null,
                new HotelsLoader.HotelsCallback() {
                    @Override
                    public void onSuccess(List<HotelData> hotels) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            if (!hotels.isEmpty()) {
                                hotelList.clear();
                                hotelList.addAll(hotels);
                                applySort();
                                cacheManager.saveHotels(hotels);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {}
                });
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Нет интернета")
                .setMessage("Для поиска отелей необходимо подключение к интернету. Показать сохраненные данные?")
                .setPositiveButton("Показать", (dialog, which) -> {
                    List<HotelData> cachedHotels = cacheManager.getHotels();
                    if (!cachedHotels.isEmpty()) {
                        hotelList.clear();
                        hotelList.addAll(cachedHotels);
                        applySort();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onItemClick(HotelData hotel) {
        Intent intent = new Intent(getContext(), HotelDetailActivity.class);
        intent.putExtra("hotel_data", hotel);
        startActivity(intent);
    }
}