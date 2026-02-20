package com.example.mobilebookingapp.dto;

import com.example.mobilebookingapp.model.HotelData;
import java.util.List;

public class HotelApiResponse {
    private List<HotelData> hotels;
    private int total;
    private String status;

    public List<HotelData> getHotels() { return hotels; }
    public void setHotels(List<HotelData> hotels) { this.hotels = hotels; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}