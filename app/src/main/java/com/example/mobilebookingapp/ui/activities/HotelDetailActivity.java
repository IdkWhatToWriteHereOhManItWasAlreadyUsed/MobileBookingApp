package com.example.mobilebookingapp.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.HotelData;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class HotelDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        HotelData hotel = (HotelData) getIntent().getSerializableExtra("hotel_data");

        if (hotel == null) {
            Toast.makeText(this, "Ошибка: отель не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация view
        ImageView imageView = findViewById(R.id.imageView);
        TextView nameView = findViewById(R.id.nameTextView);
        TextView addressView = findViewById(R.id.addressTextView);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView ratingText = findViewById(R.id.ratingText);
        ChipGroup amenitiesChipGroup = findViewById(R.id.amenitiesChipGroup);
        TextView descriptionView = findViewById(R.id.descriptionTextView);
        Button bookButton = findViewById(R.id.bookButton);

        // Заполняем данными
        nameView.setText(hotel.getName());
        addressView.setText(hotel.getAddress());
        ratingBar.setRating(hotel.getRating());
        ratingText.setText(String.valueOf(hotel.getRating()) + " / 5");
        descriptionView.setText(hotel.getDescription());

        // Заглушка для картинки
        imageView.setImageResource(android.R.drawable.ic_menu_gallery);

        // Добавляем чипсы с удобствами
        if (hotel.getAmenities() != null && !hotel.getAmenities().isEmpty()) {
            for (String amenity : hotel.getAmenities()) {
                Chip chip = new Chip(this);

                // Форматируем название удобства (wifi -> Wi-Fi, gym -> Спортзал)
                String displayName = formatAmenityName(amenity);
                chip.setText(displayName);

                chip.setClickable(false); // Просто для отображения, не нажимается
                chip.setCheckable(false);

                amenitiesChipGroup.addView(chip);
            }
        } else {
            // Если нет удобств, показываем заглушку
            Chip chip = new Chip(this);
            chip.setText("Нет информации");
            chip.setClickable(false);
            chip.setCheckable(false);
            amenitiesChipGroup.addView(chip);
        }

        bookButton.setOnClickListener(v ->
                Toast.makeText(this, R.string.booked, Toast.LENGTH_SHORT).show());
    }

    private String formatAmenityName(String amenity) {
        switch (amenity) {
            case "free_wifi": return "Wi-Fi";
            case "wifi": return "Бесплатный Wi-Fi";
            case "gym": return "Спортзал";
            case "pool": return "Бассейн";
            case "parking": return "Парковка";
            case "spa": return "Спа";
            case "restaurant": return "Ресторан";
            case "bar": return "Бар";
            case "airport_shuttle": return "Трансфер";
            case "front_desk_24h": return "Ресепшн 24/7";
            default:
                return amenity.substring(0, 1).toUpperCase() + amenity.substring(1).replace("_", " ");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}