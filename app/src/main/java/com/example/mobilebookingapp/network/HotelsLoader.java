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

    /**
     * Интерфейс колбэка для асинхронных операций
     */
    public interface HotelsCallback {
        void onSuccess(List<HotelData> hotels);
        void onError(String error);
    }

    /**
     * Асинхронный поиск отелей с фильтрами (основной метод)
     */
    public static void searchHotelsAsync(String city, String country, String countryCode,
                                         String name, Integer rating, Integer minRating,
                                         Integer limit, Integer page,
                                         HotelsCallback callback) {
        executor.execute(() -> {
            try {
                // Выполняем сетевой запрос в фоновом потоке
                String json = FakeBackendProxy.searchHotels(city, country, countryCode,
                        name, rating, minRating, limit, page);

                // Парсим JSON
                List<HotelData> hotels = parseHotelsJson(json);

                // Возвращаем результат через колбэк
                callback.onSuccess(hotels);

            } catch (Exception e) {
                e.printStackTrace();
                // В случае ошибки возвращаем тестовые данные
                List<HotelData> testHotels = getTestHotels();
                callback.onSuccess(testHotels);
                callback.onError("Ошибка загрузки: " + e.getMessage());
            }
        });
    }

    /**
     * Асинхронный поиск по городу
     */
    public static void searchByCityAsync(String city, HotelsCallback callback) {
        searchHotelsAsync(city, null, null, null, null, null, null, null, callback);
    }

    /**
     * Асинхронный поиск по минимальному рейтингу
     */
    public static void searchByMinRatingAsync(String city, int minRating, HotelsCallback callback) {
        searchHotelsAsync(city, null, null, null, null, minRating, null, null, callback);
    }

    /**
     * Асинхронный поиск по названию
     */
    public static void searchByNameAsync(String name, HotelsCallback callback) {
        searchHotelsAsync(null, null, null, name, null, null, null, null, callback);
    }

    /**
     * Парсинг JSON ответа от API
     */
    private static List<HotelData> parseHotelsJson(String json) {
        try {
            // Пробуем распарсить как ApiResponse
            ApiResponse response = gson.fromJson(json, ApiResponse.class);

            if (response != null && response.success && response.data != null) {
                return response.data;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        // Если не получилось, пробуем как прямой массив
        try {
            Type listType = new TypeToken<List<HotelData>>(){}.getType();
            List<HotelData> hotels = gson.fromJson(json, listType);
            if (hotels != null) {
                return hotels;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        // Если ничего не сработало, возвращаем тестовые данные
        return getTestHotels();
    }

    /**
     * Класс для парсинга ответа API
     */
    private static class ApiResponse {
        boolean success;
        List<HotelData> data;
        String message;
        long timestamp;
    }

    /**
     * Получение тестовых данных
     */
    private static List<HotelData> getTestHotels() {
        String testJson = FakeBackendProxy.getTestHotelsJson();
        return parseHotelsJson(testJson);
    }

    /**
     * @deprecated Используйте асинхронные методы вместо этого
     * Этот метод может вызвать NetworkOnMainThreadException
     */
    @Deprecated
    public static List<HotelData> searchHotels(String city, String country, String countryCode,
                                               String name, Integer rating, Integer minRating,
                                               Integer limit, Integer page) {
        try {
            String json = FakeBackendProxy.searchHotels(city, country, countryCode,
                    name, rating, minRating, limit, page);
            return parseHotelsJson(json);
        } catch (Exception e) {
            e.printStackTrace();
            return getTestHotels();
        }
    }

    /**
     * @deprecated Используйте searchByCityAsync
     */
    @Deprecated
    public static List<HotelData> searchByCity(String city) {
        return searchHotels(city, null, null, null, null, null, null, null);
    }

    /**
     * @deprecated Используйте searchByMinRatingAsync
     */
    @Deprecated
    public static List<HotelData> searchByMinRating(String city, int minRating) {
        return searchHotels(city, null, null, null, null, minRating, null, null);
    }

    /**
     * @deprecated Используйте searchByNameAsync
     */
    @Deprecated
    public static List<HotelData> searchByName(String name) {
        return searchHotels(null, null, null, name, null, null, null, null);
    }

    public static List<HotelData> loadFromCache(Context context) {
        CacheManager cacheManager = new CacheManager(context);
        return cacheManager.getHotels();
    }

    /**
     * Сохранить в кэш
     */
    public static void saveToCache(Context context, List<HotelData> hotels) {
        CacheManager cacheManager = new CacheManager(context);
        cacheManager.saveHotels(hotels);
    }
}