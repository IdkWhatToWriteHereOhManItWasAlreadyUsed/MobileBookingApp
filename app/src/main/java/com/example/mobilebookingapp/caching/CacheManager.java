package com.example.mobilebookingapp.caching;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.mobilebookingapp.model.HotelData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CacheManager {

    private static final String PREF_NAME = "hotel_cache";
    private static final String KEY_HOTELS = "cached_hotels";
    private static final String KEY_TIMESTAMP = "cache_timestamp";
    private static final Gson gson = new Gson();

    private SharedPreferences prefs;

    public CacheManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveHotels(List<HotelData> hotels) {
        String json = gson.toJson(hotels);
        prefs.edit()
                .putString(KEY_HOTELS, json)
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public List<HotelData> getHotels() {
        String json = prefs.getString(KEY_HOTELS, null);
        if (json == null) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<HotelData>>(){}.getType();
            List<HotelData> hotels = gson.fromJson(json, type);
            return hotels != null ? hotels : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public boolean hasCache() {
        return prefs.contains(KEY_HOTELS);
    }

    public long getCacheTimestamp() {
        return prefs.getLong(KEY_TIMESTAMP, 0);
    }

    public void clearCache() {
        prefs.edit().clear().apply();
    }

    public boolean isCacheExpired() {
        long cacheTime = getCacheTimestamp();
        long currentTime = System.currentTimeMillis();
        // 24 часа в миллисекундах
        long oneDay = 24 * 60 * 60 * 1000;
        return (currentTime - cacheTime) > oneDay;
    }
}