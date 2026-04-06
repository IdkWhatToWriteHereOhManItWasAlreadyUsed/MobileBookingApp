package com.example.mobilebookingapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.network.HotelsLoader;
import com.example.mobilebookingapp.network.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private TextInputEditText etHotelName, etCity, etCountry, etCountryCode, etLimit;
    private SeekBar ratingSeekBar;
    private TextView tvRatingValue;
    private MaterialButton btnSearch;
    private ProgressBar progressBar;

    public static final String EXTRA_SEARCH_PARAMS = "search_params";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();
        setupRatingSeekBar();
        setupSearchButton();
    }

    private void initViews() {
        etHotelName = findViewById(R.id.editHotelName);
        etCity = findViewById(R.id.editCity);
        etCountry = findViewById(R.id.editCountry);
        etCountryCode = findViewById(R.id.editCountryCode);
        etLimit = findViewById(R.id.editLimit);
        ratingSeekBar = findViewById(R.id.ratingSeekBar);
        tvRatingValue = findViewById(R.id.tvRatingValue);
        btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRatingSeekBar() {
        ratingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRatingValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }
        Map<String, String> searchParams = new HashMap<>();

        String hotelName = etHotelName.getText().toString().trim();
        if (!TextUtils.isEmpty(hotelName)) {
            searchParams.put("name", hotelName);
        }

        String city = etCity.getText().toString().trim();
        if (!TextUtils.isEmpty(city)) {
            searchParams.put("city", city);
        }

        String country = etCountry.getText().toString().trim();
        if (!TextUtils.isEmpty(country)) {
            searchParams.put("country", country);
        }

        String countryCode = etCountryCode.getText().toString().trim().toUpperCase();
        if (!TextUtils.isEmpty(countryCode)) {
            searchParams.put("country_code", countryCode);
        }

        int minRating = ratingSeekBar.getProgress();
        if (minRating > 0) {
            searchParams.put("min_rating", String.valueOf(minRating));
        }

        String limitStr = etLimit.getText().toString().trim();
        if (!TextUtils.isEmpty(limitStr)) {
            try {
                int limit = Integer.parseInt(limitStr);
                searchParams.put("limit", String.valueOf(limit));
            } catch (NumberFormatException e) {
            }
        }

        showLoading(true);

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SEARCH_PARAMS, (HashMap) searchParams);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!show);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}