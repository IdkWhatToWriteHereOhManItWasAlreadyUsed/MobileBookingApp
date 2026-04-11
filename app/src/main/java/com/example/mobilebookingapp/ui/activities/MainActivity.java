package com.example.mobilebookingapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.mobilebookingapp.R;
import com.example.mobilebookingapp.ui.fragments.HotelsFragment;
import com.example.mobilebookingapp.ui.fragments.TravelFeedFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, new HotelsFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_hotels);
            setTitle(R.string.hotels);
        }
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.navUserName);
        View navLoginButton = headerView.findViewById(R.id.navLoginButton);

        if (mAuth.getCurrentUser() != null) {
            navUserName.setText(mAuth.getCurrentUser().getEmail());
            if (navLoginButton != null) navLoginButton.setVisibility(View.GONE);
        } else {
            navUserName.setText(R.string.guest);
            if (navLoginButton != null) navLoginButton.setVisibility(View.VISIBLE);
        }

        if (navLoginButton != null) {
            navLoginButton.setOnClickListener(v -> {
                startActivity(new Intent(this, AuthActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String title = "";

        int id = item.getItemId();

        if (id == R.id.nav_hotels) {
            fragment = new HotelsFragment();
            title = getString(R.string.hotels);
        } else if (id == R.id.nav_travel_feed) {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, AuthActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            fragment = new TravelFeedFragment();
            title = getString(R.string.travel_feed);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, fragment)
                    .commit();
            setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}