package com.example.mobilebookingapp.network;

import android.content.Context;

import com.example.mobilebookingapp.caching.CacheManager;
import com.example.mobilebookingapp.model.HotelData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HotelsLoader {
    private static final Gson gson = new Gson();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface HotelsCallback {
        void onSuccess(List<HotelData> hotels);
        void onError(String error);
    }

    public static void searchHotelsAsync(String city, String country, String countryCode,
                                         String name, Integer rating, Integer minRating,
                                         Integer limit, Integer page,
                                         HotelsCallback callback)
    {
        executor.execute(() ->
        {
            try
            {
                String json = FakeBackendProxy.searchHotels(city, country, countryCode,
                        name, rating, minRating, limit, page);

                List<HotelData> hotels = parseHotelsJson(json);
                callback.onSuccess(hotels);

            }
            catch (Exception e)
            {
                e.printStackTrace();
                List<HotelData> testHotels = getTestHotels();
                callback.onSuccess(testHotels);
                callback.onError("Ошибка загрузки: " + e.getMessage());
            }
        });
    }


    public static void searchByCityAsync(String city, HotelsCallback callback) {
        searchHotelsAsync(city, null, null, null, null, null, null, null, callback);
    }


    public static void searchByMinRatingAsync(String city, int minRating, HotelsCallback callback) {
        searchHotelsAsync(city, null, null, null, null, minRating, null, null, callback);
    }


    public static void searchByNameAsync(String name, HotelsCallback callback) {
        searchHotelsAsync(null, null, null, name, null, null, null, null, callback);
    }
    private static List<HotelData> parseHotelsJson(String json) {
        try {
            ApiResponse response = gson.fromJson(json, ApiResponse.class);

            if (response != null && response.success && response.data != null) {
                return response.data;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        try {
            Type listType = new TypeToken<List<HotelData>>(){}.getType();
            List<HotelData> hotels = gson.fromJson(json, listType);
            if (hotels != null) {
                return hotels;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        return getTestHotels();
    }

    private static class ApiResponse {
        boolean success;
        List<HotelData> data;
        String message;
        long timestamp;
    }

    private static List<HotelData> getTestHotels() {
        String testJson = FakeBackendProxy.getTestHotelsJson();
        return parseHotelsJson(testJson);
    }

    public static List<HotelData> loadFromCache(Context context) {
        CacheManager cacheManager = new CacheManager(context);
        return cacheManager.getHotels();
    }
    public static void saveToCache(Context context, List<HotelData> hotels) {
        CacheManager cacheManager = new CacheManager(context);
        cacheManager.saveHotels(hotels);
    }
}