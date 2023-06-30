package com.example.bodyboost;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit);
        setupMenuNavigation();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return true;
    }
    private void setupMenuNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.fit) {
                    // Открываем активити FitActivity
                    startActivity(new Intent(FitActivity.this, FitActivity.class));
                    return true;
                }else if (id == R.id.home) {
                    // Открываем активити HomeActivity
                    startActivity(new Intent(FitActivity.this, MainActivity.class));
                    return true;
                }else if (id == R.id.profile) {
                    // Открываем активити ProfileActivity
                    startActivity(new Intent(FitActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}