package com.example.mobilebookingapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.HotelData;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<HotelData> hotels;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HotelData hotel);
    }

    public HotelAdapter(List<HotelData> hotels, OnItemClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        HotelData hotel = hotels.get(position);
        holder.nameTextView.setText(hotel.getName());
        holder.descriptionTextView.setText(hotel.getDescription());
        holder.ratingBar.setRating(hotel.getRating());
        holder.imageView.setImageResource(getImageForHotel(hotel));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(hotel));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    private int getImageForHotel(HotelData hotel) {
        int hash = hotel.getName().hashCode();
        switch (Math.abs(hash) % 4) {
            case 0: return android.R.drawable.ic_menu_gallery;
            case 1: return android.R.drawable.ic_menu_camera;
            case 2: return android.R.drawable.ic_menu_edit;
            default: return android.R.drawable.ic_menu_slideshow;
        }
    }

    static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView;
        RatingBar ratingBar;
        ImageView imageView;

        HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}