package com.example.mobilebookingapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.model.TravelPost;
import com.example.mobilebookingapp.ui.activities.AddPostActivity;
import com.example.mobilebookingapp.ui.activities.AuthActivity;
import com.example.mobilebookingapp.ui.activities.PostDetailActivity;
import com.example.mobilebookingapp.ui.adapters.TravelPostAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TravelFeedFragment extends Fragment implements TravelPostAdapter.OnPostClickListener {

    private RecyclerView recyclerView;
    private TravelPostAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration postsListener;
    private FloatingActionButton fabAddPost;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_feed, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        fabAddPost = view.findViewById(R.id.fabAddPost);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TravelPostAdapter(this);
        recyclerView.setAdapter(adapter);

        fabAddPost.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(getContext(), AuthActivity.class));
            } else {
                startActivity(new Intent(getContext(), AddPostActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPostsRealtime();
    }

    private void loadPostsRealtime() {
        postsListener = db.collection("travelPosts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;

                    List<TravelPost> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshots) {
                        TravelPost post = doc.toObject(TravelPost.class);
                        if (post != null) {
                            post.setId(doc.getId());
                            posts.add(post);
                        }
                    }
                    adapter.updatePosts(posts);

                    if (posts.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postsListener != null) {
            postsListener.remove();
        }
    }

    @Override
    public void onPostClick(TravelPost post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra("post", post);
        startActivity(intent);
    }
}