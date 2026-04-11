package com.example.mobilebookingapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.TravelPost;
import com.example.mobilebookingapp.ui.activities.PostDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TravelPostAdapter extends RecyclerView.Adapter<TravelPostAdapter.ViewHolder> {

    private List<TravelPost> posts = new ArrayList<>();
    private OnPostClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    public interface OnPostClickListener {
        void onPostClick(TravelPost post);
    }

    public TravelPostAdapter(OnPostClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelPost post = posts.get(position);
        Context context = holder.itemView.getContext();

        holder.userName.setText(post.getUserName() != null ? post.getUserName() : "Пользователь");

        if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
            Glide.with(context)
                    .load(post.getUserAvatarUrl())
                    .circleCrop()
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into(holder.userAvatar);
        }

        holder.postText.setText(post.getText());

        if (post.getCreatedAt() != null) {
            holder.postTime.setText(dateFormat.format(post.getCreatedAt()));
        }

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getText() + "\n\n— Поделился из TravelBooking");
            context.startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPostClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<TravelPost> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView userAvatar;
        TextView userName, postText, postTime;
        ImageView postImage;
        ImageButton shareButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            userName = itemView.findViewById(R.id.userName);
            postText = itemView.findViewById(R.id.postText);
            postTime = itemView.findViewById(R.id.postTime);
            postImage = itemView.findViewById(R.id.postImage);
            shareButton = itemView.findViewById(R.id.shareButton);
        }
    }
}