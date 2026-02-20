package com.example.mobilebookingapp.network;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FakeBackendProxy {

    private static final String API_KEY = "3c573a58342127e1a22ae5c55b026305ee522a8a336c7634f14211e7f4b70786";
    private static final String BASE_URL = "https://api.hotels-api.com/v1";
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Поиск отелей с фильтрами
     */
    public static String searchHotels(String city, String country, String countryCode,
                                      String name, Integer rating, Integer minRating,
                                      Integer limit, Integer page) throws IOException {
        StringBuilder url = new StringBuilder(BASE_URL + "/hotels/search?");

        if (city != null && !city.isEmpty()) {
            url.append("city=").append(city).append("&");
        }
        if (country != null && !country.isEmpty()) {
            url.append("country=").append(country).append("&");
        }
        if (countryCode != null && !countryCode.isEmpty()) {
            url.append("country_code=").append(countryCode).append("&");
        }
        if (name != null && !name.isEmpty()) {
            url.append("name=").append(name).append("&");
        }
        if (rating != null) {
            url.append("rating=").append(rating).append("&");
        }
        if (minRating != null) {
            url.append("min_rating=").append(minRating).append("&");
        }
        if (limit != null) {
            url.append("limit=").append(limit).append("&");
        }
        if (page != null) {
            url.append("page=").append(page).append("&");
        }

        // Удаляем последний & если есть
        String finalUrl = url.toString();
        if (finalUrl.endsWith("&")) {
            finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
        }

        Request request = new Request.Builder()
                .url(finalUrl)
                .addHeader("X-API-KEY", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Ошибка API: " + response.code());
            }
            return response.body().string();
        }
    }

    /**
     * Упрощенный метод для поиска по городу
     */
    public static String searchHotelsByCity(String city) throws IOException {
        return searchHotels(city, null, null, null, null, null, null, null);
    }

    /**
     * Тестовый JSON соответствующий примеру API
     */
    public static String getTestHotelsJson() {
        return "{"
                + "  \"success\": true,"
                + "  \"data\": ["
                + "    {"
                + "      \"id\": 698731,"
                + "      \"name\": \"NH Collection Madrid Abascal\","
                + "      \"city\": \"Madrid\","
                + "      \"country\": \"Spain\","
                + "      \"country_code\": \"ES\","
                + "      \"address\": \"José Abascal 47 28003 Madrid\","
                + "      \"rating\": 4,"
                + "      \"lat\": 40.438236,"
                + "      \"lng\": -3.695222,"
                + "      \"amenities\": [\"bar\", \"free_wifi\", \"front_desk_24h\", \"gym\", \"parking\", \"spa\"]"
                + "    },"
                + "    {"
                + "      \"id\": 698733,"
                + "      \"name\": \"Hyatt Regency Hesperia Madrid\","
                + "      \"city\": \"Madrid\","
                + "      \"country\": \"Spain\","
                + "      \"country_code\": \"ES\","
                + "      \"address\": \"Paseo Castellana 57 28046 Madrid\","
                + "      \"rating\": 5,"
                + "      \"lat\": 40.438854,"
                + "      \"lng\": -3.69141,"
                + "      \"amenities\": [\"airport_shuttle\", \"bar\", \"free_wifi\", \"gym\", \"restaurant\", \"spa\"]"
                + "    }"
                + "  ],"
                + "  \"message\": null,"
                + "  \"timestamp\": 1768924228"
                + "}";
    }
}