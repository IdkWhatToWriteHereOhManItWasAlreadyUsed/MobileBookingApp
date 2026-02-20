package com.example.mobilebookingapp.model;

import android.os.Build;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

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
        String input = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, ";
        String[] words = input.split(" ");

        Random random = new Random();

        String randomWord = words[random.nextInt(words.length)];

        StringBuilder result = new StringBuilder();
        int numberOfWords = 10;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            numberOfWords = random.nextInt(10, 20);
        }

        for (int i = 0; i < numberOfWords; i++) {
            result.append(words[random.nextInt(words.length)]);
            if (i < numberOfWords - 1) {
                result.append(" ");
            }
        }

        return  result.toString();
    }

    public int getImageResId() {
         return 0;
    }
}