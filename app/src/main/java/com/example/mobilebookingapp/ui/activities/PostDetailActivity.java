package com.example.mobilebookingapp.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.TravelPost;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TravelPost post = (TravelPost) getIntent().getSerializableExtra("post");

        if (post != null) {
            de.hdodenhof.circleimageview.CircleImageView avatar = findViewById(R.id.userAvatar);
            TextView userName = findViewById(R.id.userName);
            TextView postTime = findViewById(R.id.postTime);
            TextView postText = findViewById(R.id.postText);
            ImageView postImage = findViewById(R.id.postImage);

            userName.setText(post.getUserName());
            postText.setText(post.getText());

            if (post.getUserAvatarUrl() != null) {
                Glide.with(this).load(post.getUserAvatarUrl()).circleCrop().into(avatar);
            }

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                postImage.setVisibility(ImageView.VISIBLE);
                Glide.with(this).load(post.getImageUrl()).into(postImage);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}