package com.example.mobilebookingapp.utils;

import com.example.mobilebookingapp.model.HotelData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtils {

    public enum SortBy {
        NAME_ASC,
        NAME_DESC,
        RATING_ASC,
        RATING_DESC
    }

    public static void sortHotels(List<HotelData> hotels, SortBy sortBy) {
        switch (sortBy) {
            case NAME_ASC:
                Collections.sort(hotels, Comparator.comparing(HotelData::getName));
                break;
            case NAME_DESC:
                Collections.sort(hotels, Comparator.comparing(HotelData::getName).reversed());
                break;
            case RATING_ASC:
                Collections.sort(hotels, Comparator.comparingInt(HotelData::getRating));
                break;
            case RATING_DESC:
                Collections.sort(hotels, Comparator.comparingInt(HotelData::getRating).reversed());
                break;
        }
    }

    public static String getSortDisplayName(SortBy sortBy) {
        switch (sortBy) {
            case NAME_ASC:
                return "По названию (А-Я)";
            case NAME_DESC:
                return "По названию (Я-А)";
            case RATING_ASC:
                return "По рейтингу (возрастание)";
            case RATING_DESC:
                return "По рейтингу (убывание)";
            default:
                return "";
        }
    }
}