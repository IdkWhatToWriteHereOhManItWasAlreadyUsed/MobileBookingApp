package com.example.mobilebookingapp.model;

import java.io.Serializable;
import java.util.List;

public class HotelData implements Serializable {
    private int id;
    private String name;
    private String city;
    private String country;
    private String country_code;
    private String address;
    private int rating;  // теперь int (0-5)
    private double lat;
    private double lng;
    private List<String> amenities;
    private String imageUrl;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCountryCode() { return country_code; }
    public void setCountryCode(String country_code) { this.country_code = country_code; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Для совместимости со старым кодом
    public String getDescription() {
        return address + (amenities != null && !amenities.isEmpty()
                ? " · " + String.join(" · ", amenities.subList(0, Math.min(3, amenities.size())))
                : "");
    }

    public int getImageResId() {
         return 0;
    }
}